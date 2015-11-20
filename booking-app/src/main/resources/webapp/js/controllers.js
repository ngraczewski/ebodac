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

        $scope.saveScreening = function(ignoreLimitation) {
            $http.post('../booking-app/screenings/new/' + ignoreLimitation, $scope.form.screeningDto)
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
                    $scope.form.screeningDto = undefined;
                }
            })
            .error(function(response) {
                motechAlert('bookingApp.screening.scheduleError', 'bookingApp.screening.error', response);
            });
        };

        $scope.formIsFilled = function() {
            return $scope.form
                && $scope.form.screeningDto
                && $scope.form.screeningDto.volunteerName
                && $scope.form.screeningDto.date
                && $scope.form.screeningDto.startTime
                && $scope.isValidEndTime() === true
                && $scope.form.screeningDto.clinicId;
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

    controllers.controller('BookingAppClinicVisitScheduleCtrl', function ($scope, $http, $filter) {
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
