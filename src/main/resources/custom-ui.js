if(!$('#jqueryInputMaskJs').length) {
    var s = document.createElement("script");
    s.id = "jqueryInputMaskJs";
    s.type = "text/javascript";
    s.src = "../ebodac/resources/js/jquery.inputmask.js";
    $("head").append(s);
}

if(!$('#inputMaskJs').length) {
    var s = document.createElement("script");
    s.id = "inputMaskJs";
    s.type = "text/javascript";
    s.src = "../ebodac/resources/js/inputmask.js";
    $("head").append(s);
}

if ($scope.selectedEntity.name === "Participant" || $scope.selectedEntity.name === "Visit") {
    $scope.showBackToEntityListButton = false;
} else {
    $scope.showViewTrashButton = false;
    $scope.showImportButton = false;
    $scope.backToEntityList = function() {
        $scope.dataRetrievalError = false;
        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });
        $scope.selectedEntity = undefined;
        window.location.replace('#/ebodac/reports');
    };
}

if ($scope.selectedEntity.name === "ReportPrimerVaccination" || $scope.selectedEntity.name === "ReportBoosterVaccination") {
    $scope.showFieldsButton = false;
    $scope.availableExportFormats = ['csv','pdf','xls'];
    var exportEntityModal = '../ebodac/resources/partials/modals/export-entity.html';
    $scope.customModals.push(exportEntityModal);
    $scope.exportEntityInstances = function () {
        $('#exportEbodacInstanceModal').modal('show');
    };
}

$scope.showAddInstanceButton = false;
$scope.showDeleteInstanceButton = false;
var importCsvModal = '../ebodac/resources/partials/modals/import-csv.html';
var editSubjectModal = '../ebodac/resources/partials/modals/edit-subject.html';

$scope.customModals.push(importCsvModal);
$scope.customModals.push(editSubjectModal);

$scope.getMessageFromData = function(responseData) {
    var messageCode, messageParams;

    if (responseData && (typeof(responseData) === 'string')) {
        if (responseData.startsWith('key:')) {
            if (responseData.indexOf('params:') !== -1) {
               messageCode = responseData.split('\n')[0].split(':')[1];
               messageParams = responseData.split('\n')[1].split(':')[1].split(',');
            } else {
               messageCode = responseData.split(':')[1];
            }
        } else {
            messageCode = responseData;
        }
    }

    return $scope.msg(messageCode, messageParams);
};

$scope.importEntityInstances = function() {
    $('#importSubjectModal').modal('show');
};

$scope.importSubject = function () {
    blockUI();

    $('#importSubjectForm').ajaxSubmit({
        success: function () {
            $("#instancesTable").trigger('reloadGrid');
            $('#importSubjectForm').resetForm();
            $('#importSubjectModal').modal('hide');
            unblockUI();
        },
        error: function (response) {
            handleResponse('mds.error', 'mds.error.importCsv', response);
        }
    });
};

$scope.closeImportSubjectModal = function () {
    $('#importSubjectForm').resetForm();
    $('#importSubjectModal').modal('hide');
};

$scope.closeExportEbodacInstanceModal = function () {
    $('#exportEbodacInstanceForm').resetForm();
    $('#exportEbodacInstanceModal').modal('hide');
};

$scope.exportInstance = function() {
    var selectedFieldsName = [], url, sortColumn, sortDirection;

    url = "../ebodac/entities/" + $scope.selectedEntity.id + "/exportInstances";
    url = url + "?outputFormat=" + $scope.exportFormat;
    url = url + "&exportRecords=" + $scope.actualExportRecords;

   if ($scope.actualExportColumns === 'selected') {
       angular.forEach($scope.selectedFields, function(selectedField) {
           selectedFieldsName.push(selectedField.basic.displayName);
       });

       url = url + "&selectedFields=" + selectedFieldsName;
   }

   if ($scope.checkboxModel.exportWithOrder === true) {
       sortColumn = $('#instancesTable').getGridParam('sortname');
       sortDirection = $('#instancesTable').getGridParam('sortorder');

       url = url + "&sortColumn=" + sortColumn;
       url = url + "&sortDirection=" + sortDirection;
   }

   if ($scope.checkboxModel.exportWithLookup === true) {
       url = url + "&lookup=" + (($scope.selectedLookup) ? $scope.selectedLookup.lookupName : "");
       url = url + "&fields=" + JSON.stringify($scope.lookupBy);
   }

    $http.get(url)
    .success(function () {
        if ($scope.selectedEntity.name === "ReportPrimerVaccination" || $scope.selectedEntity.name === "ReportBoosterVaccination") {
            $('#exportEbodacInstanceForm').resetForm();
            $('#exportEbodacInstanceModal').modal('hide');
        } else {
            $('#exportInstanceForm').resetForm();
            $('#exportInstanceModal').modal('hide');
        }
        window.location.replace(url);
    })
    .error(function (response) {
        handleResponse('mds.error', 'mds.error.exportData', response);
    });
};

$scope.saveCurrentRecord = function() {
    $scope.currentRecord.$save(function() {
        $scope.unselectInstance();
        unblockUI();
    }, angularHandler('mds.error', 'mds.error.cannotAddInstance'));
}

$scope.addEntityInstanceDefault = function () {
    blockUI();

    var entityObject = {};

    var values = $scope.currentRecord.fields;
    angular.forEach (values, function(value, key) {
        value.value = value.value === 'null' ? null : value.value;

        if (value.name === "changed") {
            value.value = true;
        }

        if (!$scope.isAutoGenerated(value)) {
            entityObject[value.name] = value.value;
        }
    });

    if ($scope.selectedEntity.name === "Visit" && $scope.selectedInstance !== undefined) {
        motechConfirm("ebodac.reenrollVisit.confirmMsg", "ebodac.reenrollVisit.confirmTitle",
            function (response) {
                if (!response) {
                    unblockUI();
                    return;
                }
                $http.post('../ebodac/reenrollSubject', entityObject)
                .success(function(response) {
                    motechAlert(response, "ebodac.reenrollVisit.successTitle");
                    $scope.saveCurrentRecord();
                })
                .error(function(response) {
                    motechAlert("ebodac.reenrollVisit.errorMsg", "ebodac.reenrollVisit.errorTitle", $scope.getMessageFromData(response));
                    unblockUI();
                });
            });

    } else if ($scope.selectedEntity.name === "Participant" && $scope.selectedInstance !== undefined) {
        $http.post('../ebodac/subjectDataChanged', entityObject)
        .success(function(response) {
            $scope.saveCurrentRecord();
        })
        .error(function(response) {
            motechAlert("ebodac.updateSubject.errorMsg", "ebodac.updateSubject.errorTitle", $scope.getMessageFromData(response));
            unblockUI();
        });
    } else {
        $scope.saveCurrentRecord();
    }
};

$scope.addEntityInstance = function() {
    if ($scope.selectedEntity.name === "Participant") {
        var input = $("#phoneNumberForm");
        var fieldValue = input.val();
        if (fieldValue !== null && fieldValue !== undefined) {
            input.val(fieldValue.replace(/ /g, ''));
            input.trigger('input');
        }

        $http.get('../ebodac/ebodac-config')
        .success(function(response){
            if(response.showWarnings) {
                $('#editSubjectModal').modal('show');
            } else {
                $scope.addEntityInstanceDefault();
            }
        })
        .error(function(response) {
            $('#editSubjectModal').modal('show');
        });
    } else {
        $scope.addEntityInstanceDefault();
    }
};

var isPhoneNumberForm = false;

$scope.loadEditValueFormDefault = $scope.loadEditValueForm;

$scope.loadEditValueForm = function (field) {
    if(field.name === 'phoneNumber') {
        isPhoneNumberForm = true;
        return '../ebodac/resources/partials/widgets/field-phone-number.html';
    } else if (field.name === 'visits') {
        return '../ebodac/resources/partials/widgets/field-visits.html';
    }

    if(isPhoneNumberForm) {
        $("#phoneNumberForm").inputmask({ mask: "999 999 999[ 999]", greedy: false, autoUnmask: true });
        isPhoneNumberForm = false;
    }

    return $scope.loadEditValueFormDefault(field);
};

$scope.retrieveAndSetEntityData = function(entityUrl, callback) {
    $scope.lookupBy = {};
    $scope.selectedLookup = undefined;
    $scope.lookupFields = [];
    $scope.allEntityFields = [];

    blockUI();

    $http.get(entityUrl).success(function (data) {
        $scope.selectedEntity = data;

        $scope.setModuleEntity($scope.selectedEntity.module, $scope.selectedEntity.name);

        $http.get('../mds/entities/'+$scope.selectedEntity.id+'/entityFields').success(function (data) {
             $scope.allEntityFields = data;
             $scope.setAvailableFieldsForDisplay();

             if ($routeParams.entityId === undefined) {
                var hash = window.location.hash.substring(2, window.location.hash.length) + "/" + $scope.selectedEntity.id;
                $location.path(hash);
                $location.replace();
                window.history.pushState(null, "", $location.absUrl());
             }

             Entities.getAdvancedCommited({id: $scope.selectedEntity.id}, function(data) {
                $scope.entityAdvanced = data;
                $rootScope.filters = [];
                $scope.setVisibleIfExistFilters();

                if ($scope.selectedEntity.name === "Visit") {
                    $http.get("../ebodac/getLookupsForVisits")
                    .success(function(data) {
                        $scope.entityAdvanced.indexes = data;
                    });
                }

                var filterableFields = $scope.entityAdvanced.browsing.filterableFields,
                    i, field, types;
                for (i = 0; i < $scope.allEntityFields.length; i += 1) {
                    field = $scope.allEntityFields[i];

                    if ($.inArray(field.id, filterableFields) >= 0) {
                        types = $scope.filtersForField(field);

                        $rootScope.filters.push({
                            displayName: field.basic.displayName,
                            type: field.type.typeClass,
                            field: field.basic.name,
                            types: types
                        });
                    }
                }
             });

             Entities.getDisplayFields({id: $scope.selectedEntity.id}, function(data) {
                  var i, field, selectedName,
                      dbUserPreferences = $scope.getDataBrowserUserPreferencesCookie($scope.selectedEntity);

                  $scope.selectedFields = [];

                  // filter data from db
                  for (i = 0; i < data.length; i += 1) {
                      field = data[i];
                      if ($.inArray(field.basic.name, dbUserPreferences.unselected) === -1) {
                          $scope.selectedFields.push(field);
                      }
                  }

                  // additional selections
                  for (i = 0; i < dbUserPreferences.selected.length; i += 1) {
                      selectedName = dbUserPreferences.selected[i];
                      // check if already selected
                      if (!$scope.isFieldSelected(selectedName)) {
                          $scope.selectFieldByName(selectedName);
                      }
                  }

                  $scope.updateInstanceGridFields();
              });

              if (callback) {
                  callback();
              }
          });
          unblockUI();
      });
};