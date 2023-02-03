package main

import (
	"flag"
	"github.com/DependencyTrack/hyades/component-analyzer/internal/consumer"
	"github.com/DependencyTrack/hyades/component-analyzer/internal/repository"
	"github.com/DependencyTrack/hyades/component-analyzer/internal/router"
	"github.com/twmb/franz-go/pkg/kgo"
	"log"
	"strings"
)

func main() {
	var (
		seedBrokers   string
		consumerGroup string
	)
	flag.StringVar(&seedBrokers, "brokers", "", "Seed brokers")
	flag.StringVar(&consumerGroup, "consumer-group", "hyades-component-analyzer", "Consumer group")

	repo, err := repository.NewMavenRepository("https://repo1.maven.org")
	if err != nil {
		log.Fatalf("failed to setup repo: %v", err)
	}

	mavenWorker := repository.NewWorker([]repository.Repository{repo})

	group := consumer.NewGroup(map[string]func(*kgo.Client, []*kgo.Record){
		"dtrack.repo-meta-analysis.component": router.Route,
		"dtrack.repo-meta-analysis.maven":     mavenWorker.Work,
	})

	kc, err := kgo.NewClient(
		kgo.SeedBrokers(strings.Split(seedBrokers, ",")...),
		kgo.ConsumerGroup(consumerGroup),
		kgo.ConsumeTopics("dtrack.repo-meta-analysis.component", "dtrack.repo-meta-analysis.maven"),
		kgo.OnPartitionsAssigned(group.OnAssigned),
		kgo.OnPartitionsLost(group.OnRevoked),
		kgo.OnPartitionsRevoked(group.OnRevoked),
		kgo.DisableAutoCommit(),
		kgo.BlockRebalanceOnPoll(),
	)
	if err != nil {
		log.Fatalf("failed to setup kafka client: %v", err)
	}
	defer kc.Close()

	group.Start(kc)
}
