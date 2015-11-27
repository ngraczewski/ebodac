$scope.showBackToEntityListButton = false;
$scope.showAddInstanceButton = false;
$scope.showLookupButton = true;
$scope.showFieldsButton = false;
$scope.showImportButton = false;
$scope.showExportButton = false;
$scope.showViewTrashButton = false;
$scope.showFiltersButton = false;
$scope.showDeleteInstanceButton = false;

$scope.editInstance = function(id, module, entityName) {
    blockUI();
    $scope.setHiddenFilters();
    $scope.instanceEditMode = true;
    $scope.setModuleEntity(module, entityName);
    $scope.loadedFields = Instances.selectInstance({
        id: $scope.selectedEntity.id,
        param: id
        },
        function (data) {
            $scope.selectedInstance = id;
            $scope.currentRecord = data;
            $scope.fields = data.fields;
            if (entityName === "Clinic") {
                angular.forEach ($scope.fields, function(field) {
                    if (field.name === "location" || field.name === "site") {
                        field.nonEditable = true;
                    }
                });
            }

            unblockUI();
        }, angularHandler('mds.error', 'mds.error.cannotUpdateInstance'));
};
