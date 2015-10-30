(function () {
    'use strict';

    /* App Module */
    var ebodac = angular.module('ebodac', ['motech-dashboard', 'ebodac.controllers', 'ebodac.directives',
    'ebodac.services', 'ngCookies' , 'ui.bootstrap', 'mds', 'mds.utils', 'sms']), subjectId, visitId,
    reportBoosterVaccinationId, reportPrimerVaccinationId, callDetailRecordId, smsRecordId;

    $.ajax({
        url: '../mds/entities/getEntity/EBODAC Module/Participant',
        success:  function(data) {
            subjectId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/EBODAC Module/Visit',
        success:  function(data) {
            visitId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/EBODAC Module/ReportPrimerVaccination',
        success:  function(data) {
            reportPrimerVaccinationId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/EBODAC Module/ReportBoosterVaccination',
        success:  function(data) {
            reportBoosterVaccinationId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/IVR Module/CallDetailRecord',
        success:  function(data) {
            callDetailRecordId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/SMS Module/SmsRecord',
        success:  function(data) {
            smsRecordId = data.id;
        },
        async: false
    });

    ebodac.config(function ($routeProvider) {
        $routeProvider
            .when('/ebodac/subjects', { redirectTo: '/mds/dataBrowser/' + subjectId + '/ebodac' })
            .when('/ebodac/visits', { redirectTo: '/mds/dataBrowser/' + visitId + '/ebodac' })
            .when('/ebodac/settings', {templateUrl: '../ebodac/resources/partials/settings.html', controller: 'EbodacSettingsCtrl'})
            .when('/ebodac/reports', {templateUrl: '../ebodac/resources/partials/reports.html', controller: 'EbodacBasicCtrl' })
            .when('/ebodac/reportPrimerVaccination', { redirectTo: '/mds/dataBrowser/' + reportPrimerVaccinationId + '/ebodac' })
            .when('/ebodac/reportBoosterVaccination', { redirectTo: '/mds/dataBrowser/' + reportBoosterVaccinationId + '/ebodac' })
            .when('/ebodac/getReport/:reportType', { templateUrl: '../ebodac/resources/partials/report.html', controller: 'EbodacReportsCtrl' })
            .when('/ebodac/callDetailRecord', { redirectTo: '/mds/dataBrowser/' + callDetailRecordId + '/ebodac' })
            .when('/ebodac/SMSLog', { redirectTo: '/mds/dataBrowser/' + smsRecordId + '/ebodac' })
            .when('/ebodac/enrollment', {templateUrl: '../ebodac/resources/partials/enrollment.html', controller: 'EbodacEnrollmentCtrl'})
            .when('/ebodac/enrollmentAdvanced/:subjectId', {templateUrl: '../ebodac/resources/partials/enrollmentAdvanced.html', controller: 'EbodacEnrollmentAdvancedCtrl'});
    });

}());
