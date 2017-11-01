(function() {
    'use strict';

    angular
        .module('artirestApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('coordinator', {
            parent: 'entity',
            url: '/coordinator?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Coordinators'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/coordinator/coordinators.html',
                    controller: 'CoordinatorController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('coordinator-detail', {
            parent: 'coordinator',
            url: '/coordinator/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Coordinator'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/coordinator/coordinator-detail.html',
                    controller: 'CoordinatorDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Coordinator', function($stateParams, Coordinator) {
                    return Coordinator.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'coordinator',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('coordinator-detail.edit', {
            parent: 'coordinator-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/coordinator/coordinator-dialog.html',
                    controller: 'CoordinatorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Coordinator', function(Coordinator) {
                            return Coordinator.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('coordinator.new', {
            parent: 'coordinator',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/coordinator/coordinator-dialog.html',
                    controller: 'CoordinatorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                firstProcessId: null,
                                firstProcessAttr: null,
                                secondProcessId: null,
                                secondProcessAttr: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('coordinator', null, { reload: 'coordinator' });
                }, function() {
                    $state.go('coordinator');
                });
            }]
        })
        .state('coordinator.edit', {
            parent: 'coordinator',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/coordinator/coordinator-dialog.html',
                    controller: 'CoordinatorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Coordinator', function(Coordinator) {
                            return Coordinator.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('coordinator', null, { reload: 'coordinator' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('coordinator.delete', {
            parent: 'coordinator',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/coordinator/coordinator-delete-dialog.html',
                    controller: 'CoordinatorDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Coordinator', function(Coordinator) {
                            return Coordinator.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('coordinator', null, { reload: 'coordinator' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
