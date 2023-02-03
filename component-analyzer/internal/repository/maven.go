package repository

import (
	"encoding/xml"
	"fmt"
	"github.com/DependencyTrack/hyades/component-analyzer/internal/model"
	"github.com/package-url/packageurl-go"
	"net/http"
	"net/url"
	"strings"
)

type MavenMetadata struct {
	GroupID    string `xml:"groupId"`
	ArtifactID string `xml:"artifactId"`
	Versioning struct {
		Latest  string `xml:"latest"`
		Release string `xml:"release"`
	} `xml:"versioning"`
}

type MavenRepository struct {
	baseURL *url.URL
}

func NewMavenRepository(baseURL string) (*MavenRepository, error) {
	u, err := url.ParseRequestURI(baseURL)
	if err != nil {
		return nil, err
	}

	return &MavenRepository{
		baseURL: u,
	}, nil
}

func (m MavenRepository) Analyze(component model.Component) (model.RepositoryMeta, error) {
	purl, err := packageurl.FromString(component.PURL)
	if err != nil {
		return model.RepositoryMeta{}, err
	}

	if purl.Type != packageurl.TypeMaven {
		return model.RepositoryMeta{
			Component: component,
		}, nil
	}

	path, err := url.JoinPath("/maven2", strings.ReplaceAll(purl.Namespace, ".", "/"), purl.Name, "maven-metadata.xml")
	if err != nil {
		return model.RepositoryMeta{}, err
	}

	u, err := m.baseURL.Parse(path)
	if err != nil {
		return model.RepositoryMeta{}, err
	}

	req, err := http.NewRequest(http.MethodGet, u.String(), nil)
	if err != nil {
		return model.RepositoryMeta{}, err
	}

	res, err := http.DefaultClient.Do(req)
	if err != nil {
		return model.RepositoryMeta{}, err
	}
	defer res.Body.Close()

	if res.StatusCode != http.StatusOK {
		return model.RepositoryMeta{}, fmt.Errorf("unexpected status code: %d", res.StatusCode)
	}

	metadata := MavenMetadata{}
	if err := xml.NewDecoder(res.Body).Decode(&metadata); err != nil {
		return model.RepositoryMeta{}, err
	}

	latestVersion := metadata.Versioning.Latest
	if metadata.Versioning.Release != "" {
		latestVersion = metadata.Versioning.Release
	}

	return model.RepositoryMeta{
		LatestVersion: latestVersion,
		Component:     component,
	}, nil
}
