app.controller('MainCtrl', function($scope, Service, $timeout, $state){
	var self = this;
	self.test = 'TEST';
	
	self.user = localStorage.getItem('user');
	
	var map = {'Easy' : 1, 'Medium': 2, "Hard": 3};
	
	
	self.points = Service.getPoints(self.user);
	Service.getData().then(function(response){
		self.problems = response;
		self.index();
		var points = 0;
		$timeout(function(){
			$scope.$apply();
		})
	})
	

	
	

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