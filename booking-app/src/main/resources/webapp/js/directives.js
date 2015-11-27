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

    directives.directive('bookingAppDatePicker', ['$timeout', function($timeout) {

        function dateParser(date, offset) {
            var parts = date.split('-'), date;
            return new Date(parts[2], parts[1] - 1, parseInt(parts[0]) + offset);
        }

        return {
            restrict: 'A',
            scope: {
                forDate: '@'
            },
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                var isReadOnly = scope.$eval(attr.ngReadonly);
                if(!isReadOnly) {
                    angular.element(element).datepicker({
                        changeYear: true,
                        showButtonPanel: true,
                        dateFormat: 'dd-mm-yy',
                        minDate: dateParser(scope.forDate, 1),
                        maxDate: dateParser(scope.forDate, 28),
                        onSelect: function (dateTex) {
                            $timeout(function() {
                                ngModel.$setViewValue(dateTex);
                            })
                        },
                        onChangeMonthYear: function (year, month, inst) {
                            var curDate = $(this).datepicker("getDate");
                            if (curDate === null) {
                                return;
                            }
                            if (curDate.getFullYear() !== year || curDate.getMonth() !== month - 1) {
                                curDate.setYear(year);
                                curDate.setMonth(month - 1);
                                $(this).datepicker("setDate", curDate);
                            }
                        },
                        onClose: function (dateText, inst) {
                            var viewValue = element.val();
                            $timeout(function() {
                                ngModel.$setViewValue(viewValue);
                            })
                        }
                    });
                }
            }
        };
    }]);

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
                    colNames: [
                        scope.msg("bookingApp.screening.bookingId"),
                        scope.msg("bookingApp.screening.volunteerName"),
                        scope.msg("bookingApp.siteId"),
                        scope.msg("bookingApp.clinic"),
                        scope.msg("bookingApp.screening.date"),
                        scope.msg("bookingApp.startTime"),
                        scope.msg("bookingApp.endTime"),
                        ""],
                    colModel: [
                        { name: "id" },
                        { name: "volunteer.name" },
                        {
                            name: "site.siteId",
                            index: 'clinic.site.siteId'
                        },
                        { name: "clinic.location" },
                        { name: "date" },
                        { name: "startTime" },
                        { name: "endTime" },
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
                    rowNum: 50,
                    rowList: [10, 20, 50, 100],
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

                scope.$watch("lookupRefresh", function () {
                    $('#' + attrs.id).jqGrid('setGridParam', {
                        page: 1,
                        postData: {
                            fields: JSON.stringify(scope.lookupBy),
                            lookup: (scope.selectedLookup) ? scope.selectedLookup.lookupName : ""
                        }
                    }).trigger('reloadGrid');
                });

            }
        };
    });

    directives.directive('primeVaccinationGrid', function ($compile) {

        var gridDataExtension;

        function createButton(rowId) {
            return '<button type="button" class="btn btn-primary btn-sm ng-binding printBtn" ng-click="printCardFrom(' +
            rowId + ')">' + '<i class="fa fa-fw fa-print"></i>' + '</button>'
        }

        function extendGrid(cellValue, options, rowObject) {
            var rowExtraData = {};

            rowExtraData.visitBookingDetailsId = rowObject.visitBookingDetailsId;
            rowExtraData.siteId = rowObject.siteId;
            rowExtraData.clinicId = rowObject.clinicId;
            rowExtraData.visitId = rowObject.visitId;
            rowExtraData.participantGender = rowObject.participantGender;

            gridDataExtension[options.rowId] = rowExtraData;

            return cellValue;
        }

        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element);

                elem.jqGrid({
                    url: "../booking-app/primeVaccinationSchedule",
                    datatype: "json",
                    mtype: "GET",
                    colNames: [
                        scope.msg("bookingApp.primeVaccination.location"),
                        scope.msg("bookingApp.primeVaccination.participantId"),
                        scope.msg("bookingApp.primeVaccination.participantName"),
                        scope.msg("bookingApp.primeVaccination.femaleChildBearingAge"),
                        scope.msg("bookingApp.primeVaccination.screeningActualDate"),
                        scope.msg("bookingApp.primeVaccination.primeVacDate"),
                        scope.msg("bookingApp.startTime"),
                        scope.msg("bookingApp.endTime"), ""],
                    colModel: [
                        { name: "location" },
                        { name: "participantId", formatter: extendGrid },
                        { name: "participantName", },
                        { name: "femaleChildBearingAge" },
                        { name: "actualScreeningDate" },
                        { name: "date" },
                        { name: "startTime" },
                        { name: "endTime" },
                        { name: "print", align: "center", sortable: false, width: 40}
                    ],
                    gridComplete: function(){
                        var ids = elem.getDataIDs();
                            for(var i=0;i<ids.length;i++){
                                elem.setRowData(ids[i],{print: createButton(ids[i])})
                            }
                        $compile($('.printBtn'))(scope);
                        $('#primeVaccinationTable .ui-jqgrid-hdiv').addClass("table-lightblue");
                        $('#primeVaccinationTable .ui-jqgrid-btable').addClass("table-lightblue");
                    },
                    pager: "#pager",
                    rowNum: 50,
                    rowList: [10, 20, 50, 100],
                    sortname: null,
                    sortorder: "desc",
                    viewrecords: true,
                    gridview: true,
                    loadOnce: false,
                    beforeSelectRow: function() {
                        return false;
                    },
                    beforeRequest: function() {
                        gridDataExtension = [];
                    },
                    onCellSelect: function(rowId, iCol, cellContent, e) {
                        if (iCol !== 8) {
                            var rowData = elem.getRowData(rowId),
                                extraRowData = gridDataExtension[rowId];

                            scope.newForm();
                            scope.form.dto.visitBookingDetailsId = extraRowData.visitBookingDetailsId;
                            scope.form.dto.participantId = rowData.participantId;
                            scope.form.dto.participantName = rowData.participantName;
                            scope.form.dto.femaleChildBearingAge = rowData.femaleChildBearingAge;
                            scope.form.dto.actualScreeningDate = rowData.actualScreeningDate;
                            scope.form.dto.date = rowData.date;
                            scope.form.dto.startTime = rowData.startTime;
                            scope.form.dto.endTime = rowData.endTime;
                            scope.form.dto.siteId = extraRowData.siteId;
                            scope.form.dto.clinicId = extraRowData.clinicId;
                            scope.form.dto.visitId = extraRowData.visitId;
                            scope.form.dto.participantGender = extraRowData.participantGender;
                            scope.reloadSelects();
                            $('#primeVaccinationScheduleModal').modal('show');
                        }
                    }
                });

                scope.$watch("lookupRefresh", function () {
                    $('#' + attrs.id).jqGrid('setGridParam', {
                        page: 1,
                        postData: {
                            fields: JSON.stringify(scope.lookupBy),
                            lookup: (scope.selectedLookup) ? scope.selectedLookup.lookupName : ""
                        }
                    }).trigger('reloadGrid');
                });
            }
        };
    });
}());