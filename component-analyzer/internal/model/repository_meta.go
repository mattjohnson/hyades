package model

type RepositoryMeta struct {
	Component     Component `json:"component"`
	LatestVersion string    `json:"latestVersion,omitempty"`
	LastUpdated   string    `json:"lastUpdated,omitempty"`
}
