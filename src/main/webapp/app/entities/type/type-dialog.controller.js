(function() {
    'use strict';

    angular
        .module('dictionaryApp')
        .controller('TypeDialogController', TypeDialogController);

    TypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Type', 'Word'];

    function TypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Type, Word) {
        var vm = this;

        vm.type = entity;
        vm.clear = clear;
        vm.save = save;
        vm.words = Word.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.type.id !== null) {
                Type.update(vm.type, onSaveSuccess, onSaveError);
            } else {
                Type.save(vm.type, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('dictionaryApp:typeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
