app.service('Service', function ($http, $q) {

    var url = 'fakeurlplsno';
    var data;
    var self = this;
    var functions = {
        getData: function () {
            return $q(function (resolve, reject) {
                if (data) {
                    resolve(data);
                } else {
                    $http.get('/getalldata').then(function (response) {
                        data = response.data;
                        resolve(data);
                    }, function (error) {
                        reject(error);
                    })
                }
            });
        },
        getProblem: function (index) {
            return $q(function (resolve, reject) {
                functions.getData().then(function (response) {
                    if (index >= data.length) {
                        reject("wow dude calm down with that index");
                    }
                    resolve(data[index]);
                })
            })
        },
        submitSolution: function (text, index) {
            return $q(function (resolve, reject) {

                    $http.post('/sendcode',
                        {
                            'code': encodeURIComponent(text),
                            'problemNum': index
                        }
                    ).then(function (response) {
                        resolve(response);
                    }, function (error) {
                        reject(error);
                    })
                }
            )
        },
        getPoints: function (user) {
            return $q(function (resolve, reject) {
                var length = 100;
                functions.getData().then(function (response) {
                    var problems = response;
                    var points = 0;
                    var map = {'Easy': 1, 'Medium': 2, "Hard": 3};
                    self.points = 0;
                    for (var i = 0; i < length; i++) {
                        if (localStorage.getItem(user + i)) {
                            points += 100 * map[problems[i].difficulty];
                        }
                    }
                    resolve(points);
                })
            })
        }
    };
    return functions;
});