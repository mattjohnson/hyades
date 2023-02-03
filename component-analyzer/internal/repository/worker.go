package repository

import (
	"context"
	"encoding/json"
	"github.com/DependencyTrack/hyades/component-analyzer/internal/model"
	"github.com/twmb/franz-go/pkg/kgo"
	"log"
)

type Worker struct {
	repos []Repository
}

func NewWorker(repos []Repository) *Worker {
	return &Worker{
		repos: repos,
	}
}

func (w *Worker) Work(client *kgo.Client, records []*kgo.Record) {
	for _, record := range records {
		component := model.Component{}
		err := json.Unmarshal(record.Value, &component)
		if err != nil {
			log.Printf("failed to decode component: %v", err)
			continue
		}

		for _, repo := range w.repos {
			meta, err := repo.Analyze(component)
			if err != nil {
				log.Printf("failed to analyze %#v: %v", component, err)
				continue
			}

			metaBytes, err := json.Marshal(meta)
			if err != nil {
				log.Printf("failed to encode result: %v", err)
				continue
			}

			err = client.ProduceSync(context.TODO(), &kgo.Record{
				Topic: "dtrack.repo-meta-analysis.result",
				Key:   []byte(component.UUID),
				Value: metaBytes,
			}).FirstErr()
			if err != nil {
				log.Printf("failed to produce result: %v", err)
			}
		}
	}

	err := client.CommitRecords(context.TODO(), records...)
	if err != nil {
		log.Fatalf("failed to commit offsets: %v", err)
	}
}
