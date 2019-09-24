app.controller('MainCtrl', function($scope, Service, $timeout, $state){
	var self = this;
	self.test = 'TEST';
	
	Service.getData().then(function(response){
		self.problems = response;
		self.index();
		$timeout(function(){
			$scope.$apply();
		})
	})

	self.index = function(){
		for(var i=0;i<self.problems.length;i++){
			self.problems[i].index = i+1;
		}
	}
	
	self.goTo = function(index){
		console.log(index);
		$state.go("problem", {"index" : index})
	}
	

})