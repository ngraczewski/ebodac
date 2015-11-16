(function() {
    'use strict';

    var controllers = angular.module('bookingApp.controllers', []);

    controllers.controller('BAScreeningCtrl', function ($scope, $timeout, Screenings, Sites) {

        $scope.sites = Sites.query();
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
                function success() {
                    $("#screenings").trigger('reloadGrid');
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
    });
}());
