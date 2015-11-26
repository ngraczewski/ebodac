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

}());
