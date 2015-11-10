(function () {
    'use strict';

    var directives = angular.module('bookingApp.directives', []);

    directives.directive('set', function() {
        return {
            restrict: 'A',
            scope: {
                set: '@'
            },
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {

                var field = attrs.ngModel.substr(attrs.ngModel.lastIndexOf('.') + 1).split('I')[0] + "s";

                scope.$watch("$parent." + attrs.ngModel, function (id) {
                    if (id) {
                        scope.$parent[scope.set] = scope.$parent.findById(scope.$parent[field], id)[scope.set];
                    }
                });
            }
        };
    });

    directives.directive('screeningGrid', function () {

        var createButton, timeFormatter, localDateFormatter;

        createButton = function() {
            return '<button type="button" class="btn btn-primary btn-sm ng-binding"><i class="fa fa-fw fa-print"></i></button>';
        };

        timeFormatter = function(time) {
            return time.hour + ":" + time.minute;
        };

        localDateFormatter = function(date) {
            return date[2] + "." + date[1] + "." + date[0];
        };

        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element);

                elem.jqGrid({
                    url: "../booking-app/screenings",
                    datatype: "json",
                    mtype: "GET",
                    colNames: ["ID", scope.msg("bookingApp.screening.volunteerName"),
                        scope.msg("bookingApp.screening.clinic"), scope.msg("bookingApp.screening.date"),
                        scope.msg("bookingApp.screening.startTime"), scope.msg("bookingApp.screening.endTime"),
                        scope.msg("bookingApp.screening.room"), ""],
                    colModel: [
                        { name: "id" },
                        { name: "volunteer.name" },
                        { name: "clinic.location" },
                        { name: "date", formatter: localDateFormatter},
                        { name: "startTime", formatter: timeFormatter},
                        { name: "endTime", formatter: timeFormatter},
                        { name: "room.number"},
                        { name: "print", align: "center", sortable: false, width: 40}
                    ],
                    gridComplete: function(){
                            var ids = elem.getDataIDs();
                            for(var i=0;i<ids.length;i++){
                                elem.setRowData(ids[i],{print: createButton()})
                            }
                        },
                    pager: "#pager",
                    rowNum: 10,
                    rowList: [10, 20, 30],
                    sortname: null,
                    sortorder: "desc",
                    viewrecords: true,
                    gridview: true,
                    loadOnce: false,
                    beforeSelectRow: function() {
                        return false;
                    },
                    onCellSelect: scope.editScreening
                    }
                );

            }
        };
    });
}());