app.controller('ProblemCtrl', function($scope, Service, $timeout, $stateParams, $state){
	var self = this;
	self.status="";
	self.user = localStorage.getItem('user');
	self.index = $stateParams.index;
	Service.getProblem(self.index).then(function(response){
		self.problem = response;
		self.solution = response.codeSample.trim();
		var points = 0;
		for(var i=0;i<self.problems.length;i++){
			if(localStorage.getItem(self.user+i)){
				points+=100*map[self.problems[i].difficulty];
			}
		}
		self.points = points
		console.log(response);
		$timeout(function(){
			$scope.$apply();
		});
	});

	self.solution = "class Solution{\n\n}";

	self.submit = function(){
		Service.submitSolution(self.solution, self.index).then(function(response){
			self.status = response['data'];
			if(self.status = 'Correct'){
				localStorage.setItem(self.user+self.index, 'Done');
			}
			$timeout(function(){
				$scope.$apply();
			});
		}, function(error){
			console.log(error);
			self.status = 'An error has occured'
		})
	};

	self.back = function(){
		$state.go('main');
	};
});
app.filter("trust", ['$sce', function($sce) {
	return function(htmlCode){
		return $sce.trustAsHtml(htmlCode);
	}
}]);
