(function() {
    'use strict';

    angular
        .module('dictionaryApp')
        .controller('TypeDetailController', TypeDetailController);

    TypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Type', 'Word'];

    function TypeDetailController($scope, $rootScope, $stateParams, previousState, entity, Type, Word) {
        var vm = this;

        vm.type = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('dictionaryApp:typeUpdate', function(event, result) {
            vm.type = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
