app.controller('ProblemCtrl', function ($scope, Service, $timeout, $stateParams, $state) {
    var self = this;
    self.status = "";
    self.user = localStorage.getItem('user');
    self.index = $stateParams.index;
    self.loading = false;
    self.points = 0;
    self.completed = localStorage.getItem(self.user + self.index);
    Service.getProblem(self.index).then(function (response) {
        self.problem = response;
        self.solution = response.codeSample.trim();
        console.log(response);
        $timeout(function () {
            $scope.$apply();
        });
    });
    Service.getPoints(self.user).then(function (response) {
        self.points = response;
        $timeout(function () {
            $scope.$apply();
        });
    });
    self.solution = "class Solution{\n\n}";

    self.submit = function () {
        self.status = '';
        self.loading = true;
        Service.submitSolution(self.solution, self.index).then(function (response) {
            self.status = response['data'];
            self.loading = false;
            if (self.status === 'Correct') {
                localStorage.setItem(self.user + self.index, 'Done');
                self.completed = 'Done'
                Service.getPoints(self.user).then(function (response) {
                    self.points = response;
                    $timeout(function () {
                        $scope.$apply();
                    });
                });
            }
            $timeout(function () {
                $scope.$apply();
            });
        }, function (error) {
            console.log(error);
            self.loading = false;
            self.status = 'An error has occurred'
        })
    };

    self.back = function () {
        $state.go('main');
    };
});
app.filter("trust", ['$sce', function ($sce) {
    return function (htmlCode) {
        return $sce.trustAsHtml(htmlCode);
    }
}]);
