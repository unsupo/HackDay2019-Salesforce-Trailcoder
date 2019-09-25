app.controller('MainCtrl', function($scope, Service, $timeout, $state){
	var self = this;
	self.test = 'TEST';
	
	self.user = localStorage.getItem('user');
	
	var map = {'Easy' : 1, 'Medium': 2, "Hard": 3};
	
	Service.getData().then(function(response){
		self.problems = response;
		self.index();
		var points = 0;
		for(var i=0;i<self.problems.length;i++){
			if(localStorage.getItem(self.user+i)){
				points+=100*map[self.problems[i].difficulty];
			}
		}
		self.points = points
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