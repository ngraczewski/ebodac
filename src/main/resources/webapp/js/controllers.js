(function() {
    'use strict';

    /* Controllers */
    var controllers = angular.module('ebodac.controllers', []);

    $.postJSON = function(url, data, callback) {
        return jQuery.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'type': 'POST',
            'url': url,
            'data': JSON.stringify(data),
            'dataType': 'json',
            'success': callback,
        });
    };

    /*
     *
     * Basic
     *
     */
    controllers.controller('EbodacBasicCtrl', function ($scope) {
        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });
    });

    /*
     *
     * Messages
     *
     */
    controllers.controller('EbodacMessagesCtrl', function ($scope) {
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
    });

    /*
     *
     * Lookups
     *
     */
    controllers.controller('EbodacLookupsCtrl', function ($scope, $http, MDSUtils) {
        $scope.lookupBy = {};
        $scope.selectedLookup = undefined;
        $scope.lookupFields = [];

        $scope.getLookups = function(url) {
            $http.get(url)
            .success(function(data) {
                        $scope.lookups = data;
            });
        }

        /**
        * Shows/Hides lookup dialog
        */
        $scope.showLookupDialog = function() {
            $("#lookup-dialog")
            .css({'top': ($("#lookupDialogButton").offset().top - $("#main-content").offset().top)-40,
            'left': ($("#lookupDialogButton").offset().left - $("#main-content").offset().left)-70})
            .toggle();
        };

        /**
        * Marks passed lookup as selected. Sets fields that belong to the given lookup and resets lookupBy object
        * used to filter instances by given values
        */
        $scope.selectLookup = function(lookup) {
            $scope.selectedLookup = lookup;
            $scope.lookupFields = lookup.lookupFields;
            $scope.lookupBy = {};
        };

        /**
        * Removes lookup and resets all fields associated with a lookup
        */
        $scope.removeLookup = function() {
            $scope.lookupBy = {};
            $scope.selectedLookup = undefined;
            $scope.lookupFields = [];
            $scope.filterInstancesByLookup();
        };

        /**
        * Hides lookup dialog and sends signal to refresh the grid with new data
        */
        $scope.filterInstancesByLookup = function() {
            $scope.showLookupDialog();
            $scope.refreshGrid();
        };

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        $scope.buildLookupFieldName = function (field) {
            if (field.relatedName !== undefined && field.relatedName !== '' && field.relatedName !== null) {
                return field.name + "." + field.relatedName;
            }
            return field.name;
        };

        /**
        * Depending on the field type, includes proper html file containing visual representation for
        * the object type. Radio input for boolean, select input for list and text input as default one.
        */
        $scope.loadInputForLookupField = function(field) {
            var value = "default", type = "field";

            if (field.className === "java.lang.Boolean") {
                value = "boolean";
            } else if (field.className === "java.util.Collection") {
                value = "list";
            } else if (field.className === "org.joda.time.DateTime" || field.className === "java.util.Date") {
                value = "datetime";
            } else if (field.className === "org.joda.time.LocalDate") {
                value = "date";
            }

            if ($scope.isRangedLookup(field)) {
                type = "range";
                value = "date";
                if (!$scope.lookupBy[$scope.buildLookupFieldName(field)]) {
                    $scope.lookupBy[$scope.buildLookupFieldName(field)] = {min: '', max: ''};
                }
            }

            return '../ebodac/resources/partials/lookups/{0}-{1}.html'
                .format(type, value);
        };

        $scope.isRangedLookup = function(field) {
            return $scope.isLookupFieldOfType(field, 'RANGE');
        };

        $scope.isLookupFieldOfType = function(field, type) {
            var i, lookupField;
            for (i = 0; i < $scope.selectedLookup.lookupFields.length; i += 1) {
                lookupField = $scope.selectedLookup.lookupFields[i];
                if ($scope.buildLookupFieldName(lookupField) === $scope.buildLookupFieldName(field)) {
                    return lookupField.type === type;
                }
            }
        };

        $scope.getComboboxValues = function (settings) {
            var labelValues = MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.values'}], true).value, keys = [], key;
            // Check the user supplied flag, if true return string set
            if (MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.allowUserSupplied'}], true).value === true){
                return labelValues;
            } else {
                if (labelValues[0].indexOf(":") === -1) {       // there is no colon, so we are dealing with a string set, not a map
                    return labelValues;
                } else {
                    labelValues =  $scope.getAndSplitComboboxValues(labelValues);
                    for(key in labelValues) {
                        keys.push(key);
                    }
                    return keys;
                }
            }
        };

        $scope.getComboboxDisplayName = function (settings, value) {
            var labelValues = MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.values'}], true).value;
            // Check the user supplied flag, if true return string set
            if (MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.allowUserSupplied'}], true).value === true){
                return value;
            } else {
                if (labelValues[0].indexOf(":") === -1) { // there is no colon, so we are dealing with a string set, not a map
                    return value;
                } else {
                    labelValues =  $scope.getAndSplitComboboxValues(labelValues);
                    return labelValues[value];
                }
            }

        };

        $scope.getAndSplitComboboxValues = function (labelValues) {
            var doublet, i, map = {};
            for (i = 0; i < labelValues.length; i += 1) {
                doublet = labelValues[i].split(":");
                map[doublet[0]] = doublet[1];
            }
            return map;
        };
    });

    /*
     *
     * Settings
     *
     */
    controllers.controller('EbodacSettingsCtrl', function ($scope, $http, $timeout) {
        $scope.errors = [];
        $scope.messages = [];

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.availableCampaigns = [];

        $scope.campaignsChanged = function(change) {
            var value;

            if (change.added) {
                value = change.added.text;
                $scope.config.disconVacCampaignsList.push(value);
            } else if (change.removed) {
                value = change.removed.text;
                $scope.config.disconVacCampaignsList.removeObject(value);
            }
        };

        $http.get('../ebodac/ebodac-config')
            .success(function(response){
                var i;
                $scope.config = response;
                $scope.originalConfig = angular.copy($scope.config);

                $http.get('../ebodac/availableCampaigns')
                    .success(function(response){
                        $scope.availableCampaigns = response;
                        $timeout(function() {
                            $('#disconVacCampaigns').select2('val', $scope.config.disconVacCampaignsList);
                        }, 50);

                    })
                    .error(function(response) {
                        $scope.errors.push($scope.msg('ebodac.web.settings.enroll.disconVacCampaigns.error', response));
                    });
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('ebodac.web.settings.noConfig', response));
            });

        $scope.reset = function () {
            $scope.config = angular.copy($scope.originalConfig);
            $('#disconVacCampaigns').select2('val', $scope.config.disconVacCampaignsList);
        };

        function hideMsgLater(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 5000);
        }

        $scope.submit = function () {
            $http.post('../ebodac/ebodac-config', $scope.config)
                .success(function (response) {
                    $scope.config = response;
                    $scope.originalConfig = angular.copy($scope.config);
                    var index = $scope.messages.push($scope.msg('ebodac.web.settings.saved'));
                    hideMsgLater(index-1);
                })
                .error (function (response) {
                    //todo: better than that!
                    handleWithStackTrace('ebodac.error.header', 'ebodac.error.body', response);
                });
        };
    });

    /*
     *
     * Reports
     *
     */
    controllers.controller('EbodacReportsCtrl', function ($scope, $http, $controller, $routeParams) {
        $controller('EbodacLookupsCtrl', {$scope: $scope});
        $scope.reportType = $routeParams.reportType;
        $scope.reportName = "";
        $scope.availableExportRecords = ['All','10', '25', '50', '100', '250'];
        $scope.availableExportFormats = ['csv','pdf','xls'];
        $scope.actualExportRecords = 'All';
        $scope.actualExportColumns = 'All';
        $scope.exportFormat = 'csv';
        $scope.checkboxModel = {
            exportWithLookup : false,
            exportWithOrder : false
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        var url;
        switch($scope.reportType){
            case "dailyClinicVisitScheduleReport":
                url = "../ebodac/getLookupsForDailyClinicVisitScheduleReport";
                $scope.reportName = $scope.msg('ebodac.web.reports.dailyClinicVisitScheduleReport');
                break;
            case "followupsAfterPrimeInjectionReport":
                url = "../ebodac/getLookupsForFollowupsAfterPrimeInjectionReport";
                $scope.reportName = $scope.msg('ebodac.web.reports.followupsAfterPrimeInjectionReport');
                break;
            case "followupsMissedClinicVisitsReport":
                url = "../ebodac/getLookupsForFollowupsMissedClinicVisitsReport";
                $scope.reportName = $scope.msg('ebodac.web.reports.followupsMissedClinicVisitsReport');
                break;
        }
         $scope.getLookups(url);

        $scope.buildColumnModel = function (colModel) {
            var newColModel = colModel;
            for (var i in colModel) {
              if(!colModel[i].hasOwnProperty('formatoptions') && colModel[i].hasOwnProperty('formatter')) {
                newColModel[i].formatter = eval("(" + colModel[i].formatter + ")");
              }
            }
            return newColModel;
        };

        $scope.buildColumnNames = function (colNames) {
            var newColNames = colNames;
            for(var i in colNames) {
                newColNames[i] = $scope.msg(colNames[i]);
            }
            return newColNames;
        };

        $scope.exportEntityInstances = function () {
            $('#exportEbodacInstanceModal').modal('show');
        };

        $scope.changeExportRecords = function (records) {
            $scope.actualExportRecords = records;
        };

        $scope.changeExportFormat = function (format) {
            $scope.exportFormat = format;
        };

        $scope.closeExportEbodacInstanceModal = function () {
            $('#exportEbodacInstanceForm').resetForm();
            $('#exportEbodacInstanceModal').modal('hide');
        };

        /**
        * Exports selected entity's instances to CSV file
        */
        $scope.exportInstance = function() {
            var url, rows, page, sortColumn, sortDirection;

            switch($scope.reportType){
                case "dailyClinicVisitScheduleReport":
                    url = "../ebodac/exportDailyClinicVisitScheduleReport";
                    break;
                case "followupsAfterPrimeInjectionReport":
                    url = "../ebodac/exportFollowupsAfterPrimeInjectionReport";
                    break;
                case "followupsMissedClinicVisitsReport":
                    url = "../ebodac/exportFollowupsMissedClinicVisitsReport";
                    break;
            }
            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&exportRecords=" + $scope.actualExportRecords;

           if ($scope.checkboxModel.exportWithOrder === true) {
               sortColumn = $('#reportTable').getGridParam('sortname');
               sortDirection = $('#reportTable').getGridParam('sortorder');

               url = url + "&sortColumn=" + sortColumn;
               url = url + "&sortDirection=" + sortDirection;
           }

           if ($scope.checkboxModel.exportWithLookup === true) {
               url = url + "&lookup=" + (($scope.selectedLookup) ? $scope.selectedLookup.lookupName : "");
               url = url + "&fields=" + JSON.stringify($scope.lookupBy);
           }

            $http.get(url)
            .success(function () {
                $('#exportEbodacInstanceForm').resetForm();
                $('#exportEbodacInstanceModal').modal('hide');
                window.location.replace(url);
            })
            .error(function (response) {
                handleResponse('mds.error', 'mds.error.exportData', response);
            });
        };

        $scope.backToEntityList = function() {
            window.location.replace('#/ebodac/reports');
        };
    });

    /*
     *
     * Enrollment
     *
     */
    controllers.controller('EbodacEnrollmentCtrl', function ($scope, $http, $timeout, $controller) {

        $controller('EbodacMessagesCtrl', {$scope: $scope});
        $controller('EbodacLookupsCtrl', {$scope: $scope});

        var url = "../ebodac/getLookupsForEnrollments";
        $scope.getLookups(url);

        $scope.enrollInProgress = false;

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        $scope.enroll = function(subjectId) {
            motechConfirm("ebodac.enrollSubject.ConfirmMsg", "ebodac.enrollSubject.ConfirmTitle",
                function (response) {
                    if (!response) {
                        return;
                    } else {
                        $scope.enrollInProgress = true;
                        $http.post('../ebodac/enrollSubject', subjectId)
                        .success(function(response) {
                            motechAlert('ebodac.web.enrollment.enrollSubject.success', 'ebodac.web.enrollment.enrolledSubject');
                            $scope.refreshGrid();
                            $scope.enrollInProgress = false;
                        })
                        .error(function(response) {
                            motechAlert('ebodac.web.enrollment.enrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                            $scope.refreshGrid();
                            $scope.enrollInProgress = false;
                        });
                    }
                });
        }

        $scope.unenroll = function(subjectId) {
            motechConfirm("ebodac.unenrollSubject.ConfirmMsg", "ebodac.unenrollSubject.ConfirmTitle",
                function (response) {
                    if (!response) {
                        return;
                    } else {
                        $scope.enrollInProgress = true;
                        $http.post('../ebodac/unenrollSubject', subjectId)
                        .success(function(response) {
                            motechAlert('ebodac.web.enrollment.unenrollSubject.success', 'ebodac.web.enrollment.unenrolledSubject');
                            $scope.refreshGrid();
                            $scope.enrollInProgress = false;
                        })
                        .error(function(response) {
                            motechAlert('ebodac.web.enrollment.unenrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                            $scope.refreshGrid();
                            $scope.enrollInProgress = false;
                        });
                    }
                });
        }

        $scope.goToAdvanced = function(subjectId) {
            $.ajax({
                url: '../ebodac/checkAdvancedPermissions',
                success:  function(data) {
                    window.location.replace('#/ebodac/enrollmentAdvanced/' + subjectId);
                },
                async: false
            });
        }
    });

    /*
     *
     * Enrollment Advanced
     *
     */
    controllers.controller('EbodacEnrollmentAdvancedCtrl', function ($scope, $http, $timeout, $routeParams, $controller) {

        $controller('EbodacMessagesCtrl', {$scope: $scope});

        $scope.enrollInProgress = false;

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.backToEnrolments = function() {
            window.location.replace('#/ebodac/enrollment');
        }

        $scope.selectedSubjectId = $routeParams.subjectId;

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        $scope.enroll = function(campaignName) {
            $scope.enrollInProgress = true;
            $http.get('../ebodac/enrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName)
            .success(function(response) {
                motechAlert('ebodac.web.enrollment.enrollSubject.success', 'ebodac.web.enrollment.enrolledSubject');
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            })
            .error(function(response) {
                motechAlert('ebodac.web.enrollment.enrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            });
        }

        $scope.unenroll = function(campaignName) {
            $scope.enrollInProgress = true;
            $http.get('../ebodac/unenrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName)
            .success(function(response) {
                motechAlert('ebodac.web.enrollment.unenrollSubject.success', 'ebodac.web.enrollment.unenrolledSubject');
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            })
            .error(function(response) {
                motechAlert('ebodac.web.enrollment.unenrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            });
        }

        $scope.reenroll = function(campaignName, date) {
            $scope.enrollInProgress = true;
            $http.get('../ebodac/reenrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName + '/' + date)
            .success(function(response) {
                motechAlert('ebodac.web.enrollment.reenrollSubject.success', 'ebodac.web.enrollment.reenrolledSubject');
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            })
            .error(function(response) {
                motechAlert('ebodac.web.enrollment.reenrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            });
        }

        $scope.enrollWithNewDate = function(campaignName, date) {
            $scope.enrollInProgress = true;
            $http.get('../ebodac/enrollCampaignWithNewDate/' + $scope.selectedSubjectId + '/' + campaignName + '/' + date)
            .success(function(response) {
                motechAlert('ebodac.web.enrollment.enrollSubject.success', 'ebodac.web.enrollment.enrolledSubject');
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            })
            .error(function(response) {
                motechAlert('ebodac.web.enrollment.enrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            });
        }
    });

}());
