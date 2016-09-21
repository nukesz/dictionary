(function() {
    'use strict';

    angular
        .module('dictionaryApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('word', {
            parent: 'entity',
            url: '/word',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'dictionaryApp.word.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/word/words.html',
                    controller: 'WordController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('word');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('word-detail', {
            parent: 'entity',
            url: '/word/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'dictionaryApp.word.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/word/word-detail.html',
                    controller: 'WordDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('word');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Word', function($stateParams, Word) {
                    return Word.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'word',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('word-detail.edit', {
            parent: 'word-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/word/word-dialog.html',
                    controller: 'WordDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Word', function(Word) {
                            return Word.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('word.new', {
            parent: 'word',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/word/word-dialog.html',
                    controller: 'WordDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                swedish: null,
                                hungarian: null,
                                english: null,
                                comment: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('word', null, { reload: 'word' });
                }, function() {
                    $state.go('word');
                });
            }]
        })
        .state('word.edit', {
            parent: 'word',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/word/word-dialog.html',
                    controller: 'WordDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Word', function(Word) {
                            return Word.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('word', null, { reload: 'word' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('word.delete', {
            parent: 'word',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/word/word-delete-dialog.html',
                    controller: 'WordDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Word', function(Word) {
                            return Word.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('word', null, { reload: 'word' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
