(function () {
    'use strict';

    /* Directives */
    var directives = angular.module('ebodac.directives', []);

    directives.directive('timePicker', function($timeout) {
        return {
            restrict: 'A',
            require: 'ngModel',
            transclude: true,
            link: function(scope, element, attrs, ngModel) {
                $timeout(function() {
                    var elem = angular.element(element);

                    elem.datetimepicker({
                        dateFormat: "",
                        timeOnly: true,
                        timeFormat: "HH:mm",
                        onSelect: function (selectedTime) {
                            scope.$apply(function() {
                                ngModel.$setViewValue(selectedTime);
                            });
                        }
                    });
                });
            }
        };
    });

    directives.directive('datePicker', function($timeout) {
        return {
            restrict: 'A',
            require: 'ngModel',
            transclude: true,
            link: function(scope, element, attrs, ngModel) {
                $timeout(function() {
                    var elem = angular.element(element);

                    elem.datetimepicker({
                        dateFormat: "yy-mm-dd",
                        changeMonth: true,
                        changeYear: true,
                        showTimepicker: false,
                        onSelect: function (selectedDate) {
                            scope.$apply(function() {
                                ngModel.$setViewValue(selectedDate);
                            });
                        }
                    });
                });
            }
        };
    });

    directives.directive('reportGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                 $.ajax({
                    type: "GET",
                    url: "../ebodac/getReportModel/" + scope.reportType,
                    dataType: "json",
                    success: function (result) {
                        var jsonColNames = scope.buildColumnNames(result.colNames);
                        var jsonColModel = scope.buildColumnModel(result.colModel);
                        elem.jqGrid({
                            url: "../ebodac/getReport/" + scope.reportType,
                            headers: {
                                'Accept': 'application/x-www-form-urlencoded',
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            datatype: 'json',
                            mtype: "POST",
                            postData: {
                                fields: JSON.stringify(scope.lookupBy)
                            },
                            jsonReader:{
                                repeatitems: false
                            },
                            prmNames: {
                                sort: 'sortColumn',
                                order: 'sortDirection'
                            },
                            colNames: jsonColNames,
                            colModel: jsonColModel,
                            pager: '#' + attrs.reportGrid,
                            viewrecords: true,
                            loadonce: false,
                            resizeStop: function() {
                                $('.ui-jqgrid-htable').width('100%');
                                $('.ui-jqgrid-btable').width('100%');
                                elem.jqGrid('setGridWidth', '100%');
                            },
                            gridComplete: function () {
                                $('.ui-jqgrid-htable').width('100%');
                                $('.ui-jqgrid-btable').width('100%');
                                elem.jqGrid('setGridWidth', '100%');
                            }
                        });
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

    directives.directive('enrollmentGrid', function($http, $compile) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../ebodac/getEnrollments',
                    headers: {
                        'Accept': 'application/x-www-form-urlencoded',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    datatype: 'json',
                    mtype: "POST",
                    postData: {
                        fields: JSON.stringify(scope.lookupBy)
                    },
                    jsonReader:{
                        repeatitems: false
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    colNames: ['rowId', scope.msg('ebodac.web.enrollment.subjectId'), scope.msg('ebodac.web.enrollment.subjectName'),
                        scope.msg('ebodac.web.enrollment.status'), scope.msg('ebodac.web.enrollment.action')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                        name: 'subject',
                        index: 'subject.subjectId',
                        classes: 'pointer',
                        align: 'center',
                        formatter: function(cellValue, options, rowObject) {
                                       if (!cellValue){
                                           return '';
                                       }
                                       return cellValue.subjectId;
                                   }
                    }, {
                        name: 'subjectName',
                        jsonmap: 'subject',
                        index: 'subject.name',
                        classes: 'pointer',
                        align: 'center',
                        formatter: function(cellValue, options, rowObject) {
                                       if (!cellValue || !cellValue.name){
                                           return '';
                                       }
                                       return cellValue.name;
                                   }
                    }, {
                        name: 'status',
                        index: 'status',
                        classes: 'pointer',
                        align: 'center'
                    }, {
                        name: 'action',
                        jsonmap: 'status',
                        align: 'center',
                        sortable: false,
                        formatter: function(cellValue, options, rowObject) {
                                       if (rowObject.status === 'Enrolled') {
                                           return "<button ng-click='unenroll(\"" + rowObject.subject.subjectId + "\")'" +
                                                   " type='button' class='btn btn-danger compileBtn' ng-disabled='enrollInProgress'>" +
                                                   scope.msg('ebodac.web.enrollment.btn.unenroll') + "</button>";
                                       } else if (rowObject.status === 'Unenrolled' || rowObject.status === 'Initial') {
                                           return "<button ng-click='enroll(\"" + rowObject.subject.subjectId + "\")'" +
                                                   " type='button' class='btn btn-success compileBtn' ng-disabled='enrollInProgress'>" +
                                                   scope.msg('ebodac.web.enrollment.btn.enroll') + "</button>";
                                       }
                                       return '';
                                   }
                    }],
                    onCellSelect: function (id, iCol, cellContent, e) {
                        if (iCol !== 4) {
                            var rowValue = elem.jqGrid('getRowData', id);
                            scope.goToAdvanced(rowValue.subject);
                        }
                    },
                    pager: '#' + attrs.enrollmentGrid,
                    viewrecords: true,
                    loadonce: false,
                    resizeStop: function() {
                        $('.ui-jqgrid-htable').width('100%');
                        $('.ui-jqgrid-btable').width('100%');
                        elem.jqGrid('setGridWidth', '100%');
                    },
                    gridComplete: function () {
                        $('.ui-jqgrid-htable').width('100%');
                        $('.ui-jqgrid-btable').width('100%');
                        elem.jqGrid('setGridWidth', '100%');
                        $compile($('.compileBtn'))(scope);
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

    directives.directive('enrollmentAdvancedGrid', function($http, $compile) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../ebodac/getEnrollmentAdvanced/' + scope.selectedSubjectId,
                    headers: {
                        'Accept': 'application/x-www-form-urlencoded',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    datatype: 'json',
                    mtype: "POST",
                    postData: {
                    },
                    jsonReader:{
                        repeatitems: false
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    colNames: ['rowId', scope.msg('ebodac.web.enrollment.subjectId'), scope.msg('ebodac.web.enrollment.campaignName'),
                        scope.msg('ebodac.web.enrollment.date'), scope.msg('ebodac.web.enrollment.status'), scope.msg('ebodac.web.enrollment.action')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                        name: 'externalId',
                        index: 'externalId',
                        align: 'center'
                    }, {
                        name: 'campaignName',
                        index: 'campaignName',
                        align: 'center'
                    }, {
                        name: 'referenceDate',
                        index: 'referenceDate',
                        classes: 'pointer',
                        align: 'center',
                        editable: true,
                        edittype: 'text',
                        editoptions: {
                            dataInit: function(elem) {
                                $(elem).addClass("pointer");
                                $(elem).datetimepicker({
                                    dateFormat: "yy-mm-dd",
                                    changeMonth: true,
                                    changeYear: true,
                                    showTimepicker: false
                                });
                            }
                        }
                    }, {
                        name: 'status',
                        index: 'status',
                        align: 'center'
                    }, {
                        name: 'action',
                        jsonmap: 'referenceDate',
                        align: 'center',
                        sortable: false,
                        formatter: function(cellValue, options, rowObject) {
                                       if (rowObject.action !== undefined &&  rowObject.action.startsWith("<button")) {
                                           return rowObject.action;
                                       } else if (rowObject.status === 'Enrolled') {
                                           return "<button ng-click='unenroll(\"" + rowObject.campaignName + "\")'" +
                                                  " type='button' class='btn btn-danger compileBtn' ng-disabled='enrollInProgress'>" +
                                                  scope.msg('ebodac.web.enrollment.btn.unenroll') + "</button>";
                                       } else if (rowObject.status === 'Unenrolled' || rowObject.status === 'Initial') {
                                           return "<button ng-click='enroll(\"" + rowObject.campaignName + "\")'" +
                                                  " type='button' class='btn btn-success compileBtn' ng-disabled='enrollInProgress'>" +
                                                  scope.msg('ebodac.web.enrollment.btn.enroll') + "</button>";
                                       }
                                       return '';
                                   }
                    }],
                    viewrecords: true,
                    cellEdit: true,
                    cellsubmit : 'clientArray',
                    rowNum: 100,
                    loadonce: false,
                    resizeStop: function() {
                        $('.ui-jqgrid-htable').width('100%');
                        $('.ui-jqgrid-btable').width('100%');
                        elem.jqGrid('setGridWidth', '100%');
                    },
                    gridComplete: function () {
                        $('.ui-jqgrid-htable').width('100%');
                        $('.ui-jqgrid-btable').width('100%');
                        elem.jqGrid('setGridWidth', '100%');
                        $compile($('.compileBtn'))(scope);
                    },
                    beforeSaveCell: function (rowId, name, val, iRow, iCol) {
                        var rowData = elem.jqGrid('getRowData', rowId);
                        var action = '';
                        if (rowData.status === 'Enrolled') {
                            action = "<button ng-click='reenroll(\"" + rowData.campaignName + "\", \"" + val + "\")'" +
                                     " type='button' class='btn btn-primary compileBtn' ng-disabled='enrollInProgress'>" +
                                     scope.msg('ebodac.web.enrollment.btn.reenroll') + "</button>"
                        } else if (rowData.status === 'Unenrolled' || rowData.status === 'Initial') {
                            action = "<button ng-click='enrollWithNewDate(\"" + rowData.campaignName + "\", \"" + val + "\")'" +
                                     " type='button' class='btn btn-primary compileBtn' ng-disabled='enrollInProgress'>" +
                                     scope.msg('ebodac.web.enrollment.btn.enroll') + "</button>"
                        }
                        rowData.action = action;
                        elem.jqGrid('setRowData', rowId, rowData);
                        $compile($('.compileBtn'))(scope);
                        return val;
                    }
                });

                scope.$watch("lookupRefresh", function () {
                    $('#' + attrs.id).jqGrid('setGridParam', {
                        page: 1,
                        postData: {
                        }
                    }).trigger('reloadGrid');
                });
            }
        };
    });

}());
