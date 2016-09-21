(function() {
    'use strict';

    angular
        .module('dictionaryApp')
        .controller('WordDialogController', WordDialogController);

    WordDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Word', 'Type'];

    function WordDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Word, Type) {
        var vm = this;

        vm.word = entity;
        vm.clear = clear;
        vm.save = save;
        vm.types = Type.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.word.id !== null) {
                Word.update(vm.word, onSaveSuccess, onSaveError);
            } else {
                Word.save(vm.word, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('dictionaryApp:wordUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
