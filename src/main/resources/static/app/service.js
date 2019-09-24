app.service('Service', function($http, $q){
	
	var url = 'fakeurlplsno';
	var data;
	var functions = {
		getData : function(){
			return $q(function(resolve, reject){
				if(data){
					resolve(data);
				}else{
					$http.get('/getalldata').then(function(response){
						data = response.data;
						resolve(data);
					}, function(error){
						//reject(error);
						data = [{	'title' : 'Something Something', 
							'difficulty' : 'Impossible', 
							'description' : 'This gonna be some long ass text explaining the problem. Which is gonna be some old bullshit by the way. Like how do you stack N cans of beans so that no two of the same kind twice. Also there are M types of beans and also can you make this run in less then a second. For one million beans. Ooooooh watch out you better be efficient with your algorithm or you are not gonna pass.'
						 }
						];
						resolve(data);
					})
				}
			});
		},
		getProblem: function(index){
			return $q(function(resolve, reject){
				functions.getData().then(function(response){
					if(index >= data.length){
						reject("wow dude calm down with that index");
					}
					resolve(data[index]);
				})
			})
		},
		submitSolution: function(text, index){
			return $q(function(resolve, reject){
				$http.get(encodeURI('/sendcode'+"?code="+text + '&' + "problemNum=" + index)).then(function(response){
					resolve(response);
				}, function(error){
					reject(error);
				})
			}
		)}
	}
	return functions;
})