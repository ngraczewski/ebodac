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

    directives.directive('gridReloadTrigger', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {
                scope.$watch("$parent." + attrs.ngModel, function () {
                    $("#screenings").trigger('reloadGrid');
                });
            }
        };
    });

    directives.directive('screeningGrid', function ($compile) {

        function createButton(id) {
            return '<button type="button" class="btn btn-primary btn-sm ng-binding printBtn" ng-click="printRow(' +
                               id + ')"><i class="fa fa-fw fa-print"></i></button>';
        };

        function handleUndefined(value) {
            if (value == undefined) {
                value = "";
            }
            return value;
        }

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
                        scope.msg("bookingApp.screening.site"),""],
                    colModel: [
                        { name: "id" },
                        { name: "volunteer.name" },
                        { name: "clinic.location" },
                        { name: "date" },
                        { name: "startTime" },
                        { name: "endTime" },
                        {
                            name: "site.siteId",
                            index: 'clinic.site.siteId'
                        },
                        { name: "print", align: "center", sortable: false, width: 40}
                    ],
                    gridComplete: function() {
                        var ids = elem.getDataIDs();
                        for(var i = 0; i < ids.length; i++){
                            elem.setRowData(ids[i], {print: createButton(ids[i])})
                        }
                        $compile($('.printBtn'))(scope);
                        $('#screeningTable .ui-jqgrid-hdiv').addClass("table-lightblue");
                        $('#screeningTable .ui-jqgrid-btable').addClass("table-lightblue");
                    },
                    pager: "#pager",
                    rowNum: 10,
                    rowList: [10, 20, 30],
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    sortname: null,
                    sortorder: "desc",
                    viewrecords: true,
                    gridview: true,
                    loadOnce: false,
                    postData: {
                        startDate: function() {
                            return handleUndefined(scope.selectedFilter.startDate);
                        },
                        endDate: function() {
                            return handleUndefined(scope.selectedFilter.endDate);
                        },
                        dateFilter: function() {
                            return handleUndefined(scope.selectedFilter.dateFilter);
                        }
                    },
                    beforeSelectRow: function() {
                        return false;
                    },
                    onCellSelect: function (id, iCol, cellContent, e) {
                        if (iCol !== 7) {
                            scope.editScreening(id);
                        }
                    }
                });

            }
        };
    });
}());