(function() {
    'use strict';

    var controllers = angular.module('bookingApp.controllers', []);

    controllers.controller('BAScreeningCtrl', function ($scope, $timeout, $http, Screenings, Sites) {

        $scope.sites = Sites.query();
        $scope.screeningForPrint = {};
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
            $scope.form.screeningDto = {};
        };

        $scope.reloadSelects = function() {
            $timeout(function() {
                $('#siteSelect').trigger('change');
                $('#clinicSelect').trigger('change');
                $('#roomSelect').trigger('change');
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

        $scope.addScreening = function() {
            $scope.newForm("add");
            $('#screeningModal').modal('show');
            $scope.reloadSelects();
        };

        $scope.editScreening = function(id) {
            $scope.newForm("edit");
            $scope.form.screeningDto = Screenings.get({id: id}, function() {
                $scope.reloadSelects();
                $('#screeningModal').modal('show');
            });
        };

        $scope.saveScreening = function() {
            Screenings.addOrUpdate($scope.form.screeningDto,
                function success(data) {
                    $("#screenings").trigger('reloadGrid');
                    $scope.screeningForPrint = data;
                    $scope.form.screeningDto = undefined;
                });
        };

        $scope.formIsFilled = function() {
            return $scope.form
                && $scope.form.screeningDto
                && $scope.form.screeningDto.volunteerName
                && $scope.form.screeningDto.date
                && $scope.form.screeningDto.startTime
                && $scope.isValidEndTime() === true
                && $scope.form.screeningDto.clinicId
                && $scope.form.screeningDto.roomId;
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

        $scope.isValidEndTime = function() {

            if (!$scope.form.screeningDto) {
                return undefined;
            }

            var startTime = $scope.parseTime($scope.form.screeningDto.startTime),
                endTime = $scope.parseTime($scope.form.screeningDto.endTime);

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
        }

        $scope.printRow = function(id) {

            if(id >= 0) {
                var rowData = jQuery("#screenings").jqGrid ('getRowData', id);
                var participantId = rowData['id'];
                var participantName = rowData['volunteer.name'];
                var clinic = rowData['site.siteId'] + " - " + rowData['clinic.location'] + " - " + rowData["room.number"];
                var date = rowData['date'];
                var startTime = rowData['startTime'];
            } else {
                var participantId = $scope.screeningForPrint.volunteer.id;
                var participantName =  $scope.screeningForPrint.volunteer.name;
                var clinic = $scope.screeningForPrint.site.siteId + " - " + $scope.screeningForPrint.clinic.location + " - " + $scope.screeningForPrint.room.number;
                var date = $scope.screeningForPrint.date;
                var startTime = $scope.screeningForPrint.startTime;
            }

            var winPrint = window.open("../booking-app/resources/partials/volunteerCardScreening.html");
            winPrint.onload = function() {
                $('#participantId', winPrint.document).html(participantId);
                $('#participantName', winPrint.document).html(participantName);
                $('#location', winPrint.document).html(clinic);
                $('#screeningDate', winPrint.document).html(date);
                $('#apptTime', winPrint.document).html(startTime);

                winPrint.focus();
                winPrint.print();
            }
        }
    });

    controllers.controller('BookingAppClinicVisitScheduleCtrl', function ($scope, $http) {
        $scope.subjects = [];
        $scope.selectedSubject = {};
        $scope.primeVac = {};
        $scope.visitPlannedDates = {};

        $http.get('../booking-app/schedule/getSubjects')
        .success(function(data) {
            $scope.subjects = data;
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

        $scope.print = function() {
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
