(function() {
    'use strict';

    angular
        .module('dictionaryApp')
        .controller('TypeDeleteController',TypeDeleteController);

    TypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'Type'];

    function TypeDeleteController($uibModalInstance, entity, Type) {
        var vm = this;

        vm.type = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Type.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
