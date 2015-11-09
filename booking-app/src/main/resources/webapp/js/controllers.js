(function() {
    'use strict';

    /* Controllers */
    var controllers = angular.module('bookingApp.controllers', []);
    controllers.controller('BookingAppScreeningCtrl', function ($scope, Volunteers, Screenings, Sites) {

        $scope.newScreening = {};
        $scope.selectedSite = {};
        $scope.sites = {};

        Sites.query().$promise.then(function (result) {
            $scope.sites = result;
        });

        $scope.findById = function(list, id) {
            var i, parsedId = parseInt(id);

            for (i = 0; i < list.length; i += 1) {
                if (list[i].id === parsedId) {
                    return list[i];
                }
            }

            return undefined;
        };

        $scope.getClinics = function() {
            return $scope.selectedSite.clinics;
        };

        $scope.createButton = function() {
            return '<button type="button" class="btn btn-primary btn-sm ng-binding"><i class="fa fa-fw fa-print"></i></button>';
        };

        $scope.timeFormatter = function(time) {
            return time.hour + ":" + time.minute;
        };

        $scope.localDateFormatter = function(date) {
            return date[2] + "." + date[1] + "." + date[0];
        };

        $("#volunteers").jqGrid({
            url: "../booking-app/screenings",
            datatype: "json",
            mtype: "GET",
            colNames: ["ID", "Volunteer name", "Clinic", "Date", "Start Time", "End Time", "Room", ""],
            colModel: [
                { name: "id" },
                { name: "volunteer.name" },
                { name: "clinic.location" },
                { name: "date", formatter: $scope.localDateFormatter},
                { name: "startTime", formatter: $scope.timeFormatter},
                { name: "endTime", formatter: $scope.timeFormatter},
                { name: "room.number"},
                { name: "print", align: "center", sortable: false, width: 40}
            ],
            gridComplete: function(){
                    var ids = jQuery("#volunteers").getDataIDs();
                    for(var i=0;i<ids.length;i++){
                        jQuery("#volunteers").setRowData(ids[i],{print: $scope.createButton()})
                    }
                },
            pager: "#pager",
            rowNum: 10,
            rowList: [10, 20, 30],
            sortname: null,
            sortorder: "desc",
            viewrecords: true,
            gridview: true,
            loadOnce: false
        });
    });

    controllers.controller('BANewScreeningCtrl', function ($scope, Sites, Screenings) {

        $scope.clearForm = function() {
            $scope.$parent.newScreening = {};
        };

        $('#addVolunteerModal').on('hidden.bs.modal', function () {
            $scope.clearForm();
        });

        $scope.addScreening = function() {

            var screeningDto = {};
            screeningDto.id = $scope.$parent.newScreening.id;
            screeningDto.clinicId = $scope.$parent.newScreening.clinic.id;
            screeningDto.roomId = $scope.$parent.newScreening.room.id;
            screeningDto.volunteerName = $scope.$parent.newScreening.volunteerName;
            screeningDto.volunteerId = $scope.$parent.newScreening.volunteerId;
            screeningDto.date = $scope.$parent.newScreening.date;
            screeningDto.startTime = $scope.$parent.newScreening.startTime;
            screeningDto.endTime = $scope.$parent.newScreening.endTime;

            Screenings.addOrUpdate(screeningDto,
                function success() {
                    $("#volunteers").trigger('reloadGrid');
                    $scope.$parent.newScreening = null;
                });
        };

        $scope.formIsFilled = function() {
            return $scope.$parent.newScreening.volunteerName
                && $scope.$parent.newScreening.date
                && $scope.$parent.newScreening.startTime
                && $scope.isValidEndTime() === true
                && $scope.$parent.newScreening.clinic
                && $scope.$parent.newScreening.room;
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
            var startTime = $scope.parseTime($scope.$parent.newScreening.startTime),
                endTime = $scope.parseTime($scope.$parent.newScreening.endTime);

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
