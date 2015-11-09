(function () {
    'use strict';

    /* Services */

    var services = angular.module('bookingApp.services', ['ngResource']);

    services.factory('Volunteers', function($resource) {
        return $resource('../booking-app/volunteers', {}, {});
    });

    services.factory('Screenings', function($resource) {
        return $resource('../booking-app/screenings', {}, {
            'addOrUpdate': { url: '../booking-app/screenings/new', method: 'POST'},
            'get': {url: '../booking-app/screenings/:id', method: 'GET'}
        });
    });

    services.factory('Sites', function($resource) {
        return $resource('../booking-app/sites', {}, {});
    });

}());
