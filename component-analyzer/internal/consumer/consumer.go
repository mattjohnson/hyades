package consumer

import (
	"context"
	"github.com/twmb/franz-go/pkg/kgo"
	"log"
	"sync"
)

type Assignment struct {
	Topic     string
	Partition int32
}

type Consumer struct {
	client      *kgo.Client
	topic       string
	partition   int32
	handler     func(client *kgo.Client, records []*kgo.Record)
	doneChan    chan struct{}
	quitChan    chan struct{}
	recordsChan chan []*kgo.Record
}

func (c *Consumer) Start() {
	defer close(c.doneChan)

	for {
		select {
		case <-c.quitChan:
			return
		case records := <-c.recordsChan:
			c.handler(c.client, records)
		}
	}
}

type Group struct {
	consumers map[Assignment]*Consumer
	handlers  map[string]func(client *kgo.Client, records []*kgo.Record)
}

func NewGroup(handlers map[string]func(*kgo.Client, []*kgo.Record)) *Group {
	return &Group{
		consumers: make(map[Assignment]*Consumer),
		handlers:  handlers,
	}
}

func (g *Group) Start(client *kgo.Client) {
	for {
		fetches := client.PollRecords(context.TODO(), 500)
		if fetches.IsClientClosed() {
			return
		}
		fetches.EachError(func(topic string, partition int32, err error) {
			log.Fatalf("fetching %s/%d failed: %v", topic, partition, err)
		})
		fetches.EachPartition(func(partition kgo.FetchTopicPartition) {
			assignment := Assignment{partition.Topic, partition.Partition}
			g.consumers[assignment].recordsChan <- partition.Records
		})
		client.AllowRebalance()
	}
}

func (g *Group) OnAssigned(_ context.Context, client *kgo.Client, assignments map[string][]int32) {
	for topic, partitions := range assignments {
		for _, partition := range partitions {
			handler := g.handlers[topic]
			consumer := &Consumer{
				client:      client,
				topic:       topic,
				partition:   partition,
				handler:     handler,
				doneChan:    make(chan struct{}),
				quitChan:    make(chan struct{}),
				recordsChan: make(chan []*kgo.Record, 1),
			}
			g.consumers[Assignment{topic, partition}] = consumer
			go consumer.Start()
		}
	}
}

func (g *Group) OnRevoked(_ context.Context, _ *kgo.Client, revocations map[string][]int32) {
	wg := sync.WaitGroup{}
	defer wg.Wait()

	for topic, partitions := range revocations {
		for _, partition := range partitions {
			assignment := Assignment{topic, partition}
			consumer, ok := g.consumers[assignment]
			if !ok {
				log.Printf("no consumer assigned to %v", assignment)
				continue
			}
			delete(g.consumers, assignment)
			close(consumer.quitChan)
			wg.Add(1)
			go func() {
				log.Printf("waiting for consumer on %v to finish", assignment)
				<-consumer.doneChan
				wg.Done()
			}()
		}
	}
}
