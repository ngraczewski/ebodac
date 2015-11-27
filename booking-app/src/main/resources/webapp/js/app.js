(function () {
    'use strict';

    /* App Module */
    var bookingApp = angular.module('bookingApp', ['bookingApp.controllers', 'bookingApp.services',
        'bookingApp.directives', 'motech-dashboard', 'mds', 'mds.utils', 'ui.directives']), clinicId;

    $.ajax({
        url: '../mds/entities/getEntity/Booking App/Clinic',
        success:  function(data) {
            clinicId = data.id;
        },
        async: false
    });

    $.ajax({
            url: '../booking-app/available/bookingTabs',
            success:  function(data) {
                bookingApp.constant('BOOKING_AVAILABLE_TABS', data);
            },
            async:    false
        });

    bookingApp.run(function ($rootScope, BOOKING_AVAILABLE_TABS) {
            $rootScope.BOOKING_AVAILABLE_TABS = BOOKING_AVAILABLE_TABS;
        });

    bookingApp.config(function ($routeProvider) {
        $routeProvider
            .when('/bookingApp/screening', {
                templateUrl: '../booking-app/resources/partials/screening.html',
                controller: 'BookingAppScreeningCtrl'
            })
            .when('/bookingApp/primeVaccination', {
                templateUrl: '../booking-app/resources/partials/primeVaccination.html',
                controller: 'BookingAppPrimeVaccinationCtrl'
            })
            .when('/bookingApp/clinicVisitSchedule', {
                templateUrl: '../booking-app/resources/partials/clinicVisitSchedule.html',
                controller: 'BookingAppClinicVisitScheduleCtrl'
            })
            .when('/bookingApp/reschedule', {
                templateUrl: '../booking-app/resources/partials/reschedule.html',
                controller: 'BookingAppRescheduleCtrl'
            })
            .when('/bookingApp/visitLimitation', { redirectTo: '/mds/dataBrowser/' + clinicId + '/booking-app' });
    });

}());
