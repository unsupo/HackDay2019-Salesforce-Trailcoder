var app = angular.module('Hackday2019App', [
'ngResource',
'ngMaterial',
'ui.router'
])
.config(function($mdThemingProvider) {
  $mdThemingProvider.theme('default')
    .primaryPalette('blue')
    .accentPalette('grey');
});