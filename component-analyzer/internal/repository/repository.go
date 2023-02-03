package repository

import "github.com/DependencyTrack/hyades/component-analyzer/internal/model"

type Repository interface {
	Analyze(component model.Component) (model.RepositoryMeta, error)
}
