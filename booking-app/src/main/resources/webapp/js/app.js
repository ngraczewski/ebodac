(function () {
    'use strict';

    /* App Module */
    var bookingApp = angular.module('bookingApp', ['bookingApp.controllers', 'bookingApp.services', 'motech-dashboard', 'mds.directives']), volunteerId;

    $.ajax({
        url: '../mds/entities/getEntity/Booking App/Volunteer',
        success:  function(data) {
            volunteerId = data.id;
        },
        async: false
    });

    bookingApp.config(function ($routeProvider) {
        $routeProvider
            .when('/bookingApp/screening', { templateUrl: '../booking-app/resources/partials/screening.html', controller: 'BookingAppScreeningCtrl' })
            .when('/bookingApp/primeVaccination', { templateUrl: '../booking-app/resources/partials/primeVaccination.html', controller: 'BookingAppPrimeVaccinationCtrl' })
            .when('/bookingApp/clinicVisitSchedule', { templateUrl: '../booking-app/resources/partials/clinicVisitSchedule.html', controller: 'BookingAppClinicVisitScheduleCtrl' })
            .when('/bookingApp/reschedule', { templateUrl: '../booking-app/resources/partials/reschedule.html', controller: 'BookingAppRescheduleCtrl' });
    });

}());
