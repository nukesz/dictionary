'use strict';

describe('Controller Tests', function() {

    describe('Type Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockType, MockWord;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockType = jasmine.createSpy('MockType');
            MockWord = jasmine.createSpy('MockWord');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Type': MockType,
                'Word': MockWord
            };
            createController = function() {
                $injector.get('$controller')("TypeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'dictionaryApp:typeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
