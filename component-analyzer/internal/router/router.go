package router

import (
	"bytes"
	"context"
	"encoding/json"
	"github.com/DependencyTrack/hyades/component-analyzer/internal/model"
	"github.com/package-url/packageurl-go"
	"github.com/twmb/franz-go/pkg/kgo"
	"log"
)

func Route(client *kgo.Client, records []*kgo.Record) {
	for _, record := range records {
		component := model.Component{}
		if err := json.NewDecoder(bytes.NewReader(record.Value)).Decode(&component); err != nil {
			log.Printf("failed to decode %#v", record)
			continue
		}

		purl, err := packageurl.FromString(component.PURL)
		if err != nil {
			continue
		}

		destination := ""
		switch purl.Type {
		case packageurl.TypeMaven:
			destination = "dtrack.repo-meta-analysis.maven"
		case packageurl.TypeNPM:
			destination = "dtrack.repo-meta-analysis.npm"
		default:
			log.Printf("unsupported component type %s", purl.Type)
			continue
		}

		purlCoordinates := packageurl.NewPackageURL(
			purl.Type,
			purl.Namespace,
			purl.Name,
			purl.Version,
			nil,
			"",
		)

		if err := client.ProduceSync(context.TODO(), &kgo.Record{
			Topic: destination,
			Key:   []byte(purlCoordinates.String()),
			Value: record.Value,
		}).FirstErr(); err != nil {
			log.Printf("failed to produce to %s: %v", destination, err)
		}
	}

	err := client.CommitRecords(context.TODO(), records...)
	if err != nil {
		log.Printf("failed to commit offsets: %v", err)
	}
}
