(function () {
    'use strict';

    /* Services */

    var services = angular.module('bookingApp.services', ['ngResource']);

    services.factory('Screenings', function($resource) {
        return $resource('../booking-app/screenings', {}, {
            'get': {url: '../booking-app/screenings/:id', method: 'GET'},
            'getDefaultFilter': {url: '../booking-app/screenings/getDefaultDateFilter', method: 'GET'}
        });
    });

    services.factory('Sites', function($resource) {
        return $resource('../booking-app/sites', {}, {});
    });

    services.factory('PrimeVaccinationSchedule', function($resource) {
        return $resource('../booking-app/primeVaccinationSchedule', {}, {
            'addOrUpdate': { url: '../booking-app/primeVaccinationSchedule', method: 'POST' },
        });
    });

}());
