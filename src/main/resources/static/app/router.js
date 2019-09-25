angular.module('Hackday2019App').config(
	function($stateProvider, $urlRouterProvider, $locationProvider){
	
	$urlRouterProvider.otherwise('/main');
	$stateProvider.state('main', {
		url: '/main',
		templateUrl: './app/main.html',
		controller: 'MainCtrl'
	}).state('problem', {
		url: '/problem/{index}',
		templateUrl: './app/problem.html',
		controller: 'ProblemCtrl'
	}).state('login', {
		url: '/login',
		templateUrl: '/app/login/login.html'
	})
	
})