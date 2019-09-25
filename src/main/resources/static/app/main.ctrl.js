app.controller('MainCtrl', function($scope, Service, $timeout, $state){
	var self = this;
	self.test = 'TEST';

	self.user = localStorage.getItem('user');




	Service.getPoints(self.user).then(function(response){
		self.points = response;
		$timeout(function(){
			$scope.$apply();
		});
	});
	Service.getData().then(function(response){
		self.problems = response;
		self.index();
		var points = 0;
		$timeout(function(){
			$scope.$apply();
		})
	});





	self.index = function(){
		for(var i=0;i<self.problems.length;i++){
			self.problems[i].index = i+1;
		}
	}

	self.completed = function(index){
		if(localStorage.getItem(self.user+index)){
			return true;
		}
		return false;
	}

	self.goTo = function(index){
		console.log(index);
		$state.go("problem", {"index" : index})
	}


})