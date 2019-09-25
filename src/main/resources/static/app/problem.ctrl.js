app.controller('ProblemCtrl', function($scope, Service, $timeout, $stateParams, $state){
	var self = this;
	self.index = $stateParams.index;
	Service.getProblem(self.index).then(function(response){
		self.problem = response;
		self.solution = response.codeSample.trim();
		console.log(response);
		$timeout(function(){
			$scope.$apply();
		});
	});

	self.solution = "class Solution{\n\n}";

	self.submit = function(){
		Service.submitSolution(self.solution, self.index).then(function(response){
			self.status = response['data'];
			$timeout(function(){
				$scope.$apply();
			});
		}, function(error){
			console.log(error);
			self.status = 'Oopsie Doopsie'
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
