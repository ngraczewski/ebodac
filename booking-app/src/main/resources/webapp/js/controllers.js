(function() {
    'use strict';

    var controllers = angular.module('bookingApp.controllers', []);

    controllers.controller('BookingAppBaseCtrl', function ($scope, $timeout, $http, Screenings, Sites) {

        $scope.availableExportRecords = ['All','10', '25', '50', '100', '250'];
        $scope.availableExportFormats = ['pdf','xls'];
        $scope.actualExportRecords = 'All';
        $scope.actualExportColumns = 'All';
        $scope.exportFormat = 'pdf';
        $scope.checkboxModel = {
            exportWithLookup : true,
            exportWithOrder : false,
            exportWithFilter : true
        };

        $scope.exportEntityInstances = function () {
            $scope.checkboxModel.exportWithLookup = true;
            $scope.checkboxModel.exportWithFilter = true;
            $('#exportBookingAppInstanceModal').modal('show');
        };

        $scope.changeExportRecords = function (records) {
            $scope.actualExportRecords = records;
        };

        $scope.changeExportFormat = function (format) {
            $scope.exportFormat = format;
        };

        $scope.closeExportEbodacInstanceModal = function () {
            $('#exportBookingAppInstanceModal').resetForm();
            $('#exportBookingAppInstanceModal').modal('hide');
        };

        $scope.exportInstanceWithUrl = function(url) {
            if ($scope.selectedLookup !== undefined && $scope.checkboxModel.exportWithLookup === true) {
                url = url + "&lookup=" + (($scope.selectedLookup) ? $scope.selectedLookup.lookupName : "");
                url = url + "&fields=" + encodeURIComponent(JSON.stringify($scope.lookupBy));
            }

            $http.get(url)
            .success(function () {
                $('#exportBookingAppInstanceModal').resetForm();
                $('#exportBookingAppInstanceModal').modal('hide');
                window.location.replace(url);
            })
            .error(function (response) {
                handleResponse('mds.error', 'mds.error.exportData', response);
            });
        };

        $scope.sites = Sites.query();
        $scope.screeningForPrint = {};

        $scope.reloadSelects = function() {
            $timeout(function() {
                $('#siteSelect').trigger('change');
                $('#clinicSelect').trigger('change');
            });
        };

        $scope.findById = function(list, id) {
            var i, parsedId = parseInt(id);

            for (i = 0; i < list.length; i += 1) {
                if (list[i].id === parsedId) {
                    return list[i];
                }
            }

            return undefined;
        };

        $scope.parseTime = function(string) {

            if (string === undefined || string === "") {
                return string;
            }

            var split = string.split(":"),
                time = {};

            time.hours = parseInt(split[0]);
            time.minutes = parseInt(split[1]);

            return time;
        };

        $scope.isValidEndTime = function(startTimeString, endTimeString) {

            var startTime = $scope.parseTime(startTimeString),
                endTime = $scope.parseTime(endTimeString);

            if (startTime === undefined || endTime === undefined) {
                return undefined;
            }

            if (endTime === "") {
                return false;
            }

            if (startTime.hours === endTime.hours) {
                return startTime.minutes < endTime.minutes;
            }

            return startTime.hours < endTime.hours;
        };

        $scope.getCurrentDate = function() {

            function parseToTwoDigits(number) {
                if (number < 10) {
                    return "0" + number;
                }
                return number;
            }

            var date = new Date(),
                dateString = "",
                month = parseToTwoDigits(date.getMonth() + 1),
                day = parseToTwoDigits(date.getDate()),
                hours = parseToTwoDigits(date.getHours()),
                minutes = parseToTwoDigits(date.getMinutes());

            return date.getFullYear() + "-" + month + "-" + day + " " + hours + ":" + minutes;
        };

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
                if (!$scope.lookupBy[$scope.buildLookupFieldName(field)]) {
                    $scope.lookupBy[$scope.buildLookupFieldName(field)] = {min: '', max: ''};
                }
            }

            return '../booking-app/resources/partials/lookups/{0}-{1}.html'.format(type, value);
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

    controllers.controller('BookingAppScreeningCtrl', function ($scope, $timeout, $http, Screenings, Sites) {

        $scope.selectedFilter = {};

        $scope.filters = [{
            name: $scope.msg('bookingApp.screening.today'),
            dateFilter: "TODAY"
        },{
            name: $scope.msg('bookingApp.screening.tomorrow'),
            dateFilter: "TOMORROW"
        },{
            name: $scope.msg('bookingApp.screening.thisWeek'),
            dateFilter: "THIS_WEEK"
        },{
            name: $scope.msg('bookingApp.screening.dateRange'),
            dateFilter: "DATE_RANGE"
        }];

        $scope.selectFilter = function(value) {
            $scope.selectedFilter = $scope.filters[value];
            if (value !== 3) {
                $("#screenings").trigger('reloadGrid');
            }
        };

        Screenings.getDefaultFilter({}, function (value) {
            var i;
            for (i = 0; i < $scope.filters.length; i += 1) {
                if ($scope.filters[i].dateFilter == value.response) {
                    $scope.selectFilter(i);
                }
            }
        });

        $scope.newForm = function(type) {
            $scope.form = {};
            $scope.form.type = type;
            $scope.form.dto = {};
        };

        $scope.addScreening = function() {
            $scope.newForm("add");
            $('#screeningModal').modal('show');
            $scope.reloadSelects();
        };

        $scope.editScreening = function(id) {
            $scope.newForm("edit");
            $scope.form.dto = Screenings.get({id: id}, function() {
                $scope.reloadSelects();
                $('#screeningModal').modal('show');
            });
        };

        $scope.saveScreening = function(ignoreLimitation) {
            var confirmMsg;

            function sendRequest() {
                $http.post('../booking-app/screenings/new/' + ignoreLimitation, $scope.form.dto)
                    .success(function(data) {
                        if (data && (typeof(data) === 'string')) {
                            jConfirm($scope.msg('bookingApp.screening.confirmMsg', data), $scope.msg('bookingApp.screening.confirmTitle'),
                                function (response) {
                                    if (response) {
                                        $scope.saveScreening(true);
                                    }
                                });
                        } else {
                            $("#screenings").trigger('reloadGrid');
                            $scope.screeningForPrint = data;
                            $scope.form.dto = undefined;
                        }
                    })
                    .error(function(response) {
                        motechAlert('bookingApp.screening.scheduleError', 'bookingApp.error', response);
                    });
            }

            if ($scope.form.type == "add") {
                confirmMsg = "bookingApp.screening.confirm.shouldScheduleScreening";
            } else if ($scope.form.type == "edit") {
                confirmMsg = "bookingApp.screening.confirm.shouldUpdateScreening";
            }

            if (ignoreLimitation) {
                sendRequest();
            } else {
                motechConfirm(confirmMsg, "bookingApp.confirm", function(confirmed) {
                    if (confirmed) {
                        sendRequest();
                    }
                });
            }
        };

        $scope.formIsFilled = function() {
            return $scope.form
                && $scope.form.dto
                && $scope.form.dto.volunteerName
                && $scope.form.dto.date
                && $scope.form.dto.startTime
                && $scope.isValidEndTime($scope.form.dto.startTime, $scope.form.dto.endTime) === true
                && $scope.form.dto.clinicId
        };

        $scope.exportInstance = function() {
            var sortColumn, sortDirection, url = "../booking-app/exportInstances/screening";
            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&exportRecords=" + $scope.actualExportRecords;

            if ($scope.checkboxModel.exportWithFilter === true) {
                url = url + "&dateFilter=" + $scope.selectedFilter.dateFilter;

                if ($scope.selectedFilter.startDate) {
                    url = url + "&startDate=" + $scope.selectedFilter.startDate;
                }

                if ($scope.selectedFilter.endDate) {
                    url = url + "&endDate=" + $scope.selectedFilter.endDate;
                }
            }

            if ($scope.checkboxModel.exportWithOrder === true) {
                sortColumn = $('#screenings').getGridParam('sortname');
                sortDirection = $('#screenings').getGridParam('sortorder');

                url = url + "&sortColumn=" + sortColumn;
                url = url + "&sortDirection=" + sortDirection;
            }

            $scope.exportInstanceWithUrl(url);
        };

        $scope.printRow = function(id) {

            if(id >= 0) {
                var rowData = jQuery("#screenings").jqGrid ('getRowData', id);
                var bookingId = rowData['id'];
                var volunteerName = rowData['volunteer.name'];
                var date = rowData['date'];
            } else {
                var bookingId = $scope.screeningForPrint.volunteer.id;
                var volunteerName =  $scope.screeningForPrint.volunteer.name;
                var date = $scope.screeningForPrint.date;
            }

            var winPrint = window.open("../booking-app/resources/partials/volunteerCardScreening.html");
            winPrint.onload = function() {
                $('#bookingId', winPrint.document).html(bookingId);
                $('#volunteerName', winPrint.document).html(volunteerName);
                $('#screeningDate', winPrint.document).html(date);

                winPrint.focus();
                winPrint.print();
            }
        }
    });

    controllers.controller('BookingAppPrimeVaccinationCtrl', function ($scope, $timeout, $http) {

        $scope.getLookups("../booking-app/getLookupsForPrimeVaccinationSchedule");

        $scope.newForm = function() {
            $scope.form = {};
            $scope.form.dto = {};
        };

        $scope.savePrimeVaccinationSchedule = function(ignoreLimitation) {

            function sendRequest() {
                $http.post('../booking-app/primeVaccinationSchedule/' + ignoreLimitation, $scope.form.dto)
                    .success(function(data) {
                        if (data && (typeof(data) === 'string')) {
                            jConfirm($scope.msg('bookingApp.primeVaccination.confirmMsg', data), $scope.msg('bookingApp.primeVaccination.confirmTitle'),
                                function (response) {
                                    if (response) {
                                        $scope.savePrimeVaccinationSchedule(true);
                                    }
                                });
                        } else {
                            $("#primeVaccinationSchedule").trigger('reloadGrid');
                            $scope.form.dto = undefined;
                        }
                    })
                    .error(function(response) {
                        motechAlert('bookingApp.primeVaccination.updateError', 'bookingApp.error', response);
                    });
            }

            if (ignoreLimitation) {
                sendRequest();
            } else {
                motechConfirm("bookingApp.primeVaccination.confirm.shouldUpdatePrimeVaccination",
                              "bookingApp.confirm", function(confirmed) {
                    sendRequest();
                })
            }
        };

        $scope.reloadSelects = function() {
            $timeout(function() {
                $scope.$parent.reloadSelects();
                $('#femaleChildBearingAgeSelect').trigger('change');
            });
        };

        $scope.formIsFilled = function() {
            return $scope.form
                && $scope.form.dto
                && $scope.form.dto.date
                && $scope.form.dto.startTime
                && $scope.form.dto.clinicId
                && $scope.isValidEndTime($scope.form.dto.startTime, $scope.form.dto.endTime) === true
                && $scope.form.dto.participantGender == 'Female' ? $scope.form.dto.femaleChildBearingAge : true;
        };

        $scope.exportInstance = function() {
            var sortColumn, sortDirection, url = "../booking-app/exportInstances/primeVaccinationSchedule";
            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&exportRecords=" + $scope.actualExportRecords;

            $scope.exportInstanceWithUrl(url);
        };

        $scope.printCardFrom = function(source) {

            var rowData;

            if(source === "updated") {
                rowData = $scope.form.updated;
            } else {
                rowData = $("#primeVaccinationSchedule").getRowData(source);
            }

            var winPrint = window.open("../booking-app/resources/partials/primeVaccinationCard.html");
            winPrint.onload = function() {
                $('#versionDate', winPrint.document).html($scope.getCurrentDate());
                $('#location', winPrint.document).html(rowData.location);
                $('#participantId', winPrint.document).html(rowData.participantId);
                $('#name', winPrint.document).html(rowData.participantName);
                $('#primeVaccinationDate', winPrint.document).html(rowData.date);
                $('#appointmentTime', winPrint.document).html(rowData.startTime);

                winPrint.focus();
                winPrint.print();
            }
        };
    });

    controllers.controller('BookingAppClinicVisitScheduleCtrl', function ($scope, $http, $filter) {
        $scope.screeningVisits = [];
        $scope.selectedSubject = {};
        $scope.primeVac = {};
        $scope.visitPlannedDates = {};

        $http.get('../booking-app/schedule/getScreeningVisits')
        .success(function(data) {
            $scope.screeningVisits = data;
        });

        $scope.subjectChanged = function() {
            if ($scope.checkSubject()) {
                $http.get('../booking-app/schedule/getPrimeVacDate/' + $scope.selectedSubject.subjectId)
                .success(function(data) {
                    $scope.primeVac.date = data;
                });
            }
        }

        $scope.$watch('primeVac.date', function(newVal, oldVal) {
            if ($scope.checkSubject()) {
                $http.get('../booking-app/schedule/getPlannedDates/' + $scope.selectedSubject.subjectId + '/' + newVal)
                .success(function(data) {
                    $scope.visitPlannedDates = data;
                })
                .error(function(response) {
                    motechAlert('bookingApp.schedule.plannedDates.calculate.error', 'bookingApp.schedule.error', response);
                });
            }
        });

        $scope.save = function() {
            motechConfirm("bookingApp.schedule.confirm.shouldSaveDates", "bookingApp.confirm",
                          function(confirmed) {
                if (confirmed) {
                    if ($scope.checkSubjectAndPrimeVacDate()) {
                        $http.get('../booking-app/schedule/savePlannedDates/' + $scope.selectedSubject.subjectId + '/' + $scope.primeVac.date)
                        .success(function(response) {
                            motechAlert('bookingApp.schedule.plannedDates.saved', 'bookingApp.schedule.saved.success');
                        })
                        .error(function(response) {
                            motechAlert('bookingApp.schedule.plannedDates.save.error', 'bookingApp.schedule.error', response);
                        });
                    }
                }
            });
        }

        $scope.print = function() {
            if ($scope.checkSubjectAndPrimeVacDate()) {
                var winPrint = window.open("../booking-app/resources/partials/visitScheduleCard.html");

                winPrint.onload = function() {
                    $('#versionDate', winPrint.document).html($filter('date')(new Date(), 'yyyy-MM-dd HH:mm'));
                    $('#subjectId', winPrint.document).html($scope.selectedSubject.subjectId);
                    $('#subjectName', winPrint.document).html($scope.selectedSubject.name);
                    $('#primeActualDate', winPrint.document).html($scope.primeVac.date);
                    $('#primeVacFollowup', winPrint.document).html($scope.visitPlannedDates.PRIME_VACCINATION_FOLLOW_UP_VISIT);
                    $('#boostVacDay', winPrint.document).html($scope.visitPlannedDates.BOOST_VACCINATION_DAY);
                    $('#boostFirstFollowup', winPrint.document).html($scope.visitPlannedDates.BOOST_VACCINATION_FIRST_FOLLOW_UP_VISIT);
                    $('#boostSecondFollowup', winPrint.document).html($scope.visitPlannedDates.BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT);
                    $('#boostThirdFollowup', winPrint.document).html($scope.visitPlannedDates.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT);
                    $('#firstLongTerm', winPrint.document).html($scope.visitPlannedDates.FIRST_LONG_TERM_FOLLOW_UP_VISIT);
                    $('#secondLongTerm', winPrint.document).html($scope.visitPlannedDates.SECOND_LONG_TERM_FOLLOW_UP_VISIT);
                    $('#thirdLongTerm', winPrint.document).html($scope.visitPlannedDates.THIRD_LONG_TERM_FOLLOW_UP_VISIT);

                    winPrint.focus();
                    winPrint.print();
                }
            }
        }

        $scope.cancel = function() {
            $scope.subjectChanged();
        }

        $scope.checkSubject = function() {
            return $scope.selectedSubject !== undefined && $scope.selectedSubject !== null && $scope.selectedSubject.subjectId !== undefined;
        }

        $scope.checkSubjectAndPrimeVacDate = function() {
            return $scope.checkSubject() && $scope.primeVac.date !== undefined && $scope.primeVac.date !== null && $scope.primeVac.date !== "";
        }

    });

}());
