var app = angular.module('migManagerApp', ["ngRoute","ui.knob"])
    .config(function ($routeProvider, $locationProvider, $httpProvider) {

        $routeProvider.when('/state',
            {
                templateUrl: 'view/state.html',
                controller: 'migManagerAppCtrl'
            });
        $routeProvider.when('/users',
            {
                templateUrl: 'view/users.html',
                controller: 'UsersCtrl'
            });
        $routeProvider.when('/recoveryStatus',
            {
                templateUrl: 'view/recoveryStatus.html',
                controller: 'RecoveryStatusCtrl'
            });
        $routeProvider.when('/eventsLog',
            {
                templateUrl: 'view/eventsLog.html',
                controller: 'EventsLogCtrl'
            });
        $routeProvider.otherwise(
            {
                redirectTo: 'view/state.html',
                controller: 'migManagerAppCtrl',
            }
        )
    })

    .controller('migManagerAppCtrl', function ($scope, $http, $interval) {


        $scope.getEnvCurrentStatus = function () {

            var headers = {
                "Content-Type": "application/json"
            };

            $http({
                method: 'GET',
                url: "/v1/migration-manager/env-state/current",
                headers: headers
            }).then(function (response) {

                $scope.stateValid = true


                switch(response.data) {

                    case "00":
                        $scope.genActive = false
                        $scope.feedbackActive = false
                        break;
                    case "01":
                        $scope.genActive = false
                        $scope.feedbackActive = true
                        break;
                    case "10":
                        $scope.genActive = true
                        $scope.feedbackActive = false
                        break;
                    case "11":
                        $scope.genActive = true
                        $scope.feedbackActive = true
                        break;
                    default:
                        $scope.genActive = false
                        $scope.feedbackActive = false
                }

                console.log(response);
            }, function (response) {
                $scope.genActive = null;
                $scope.feedbackActive = null;
                console.log(response);
            });
        }

    if ($scope.stateValid == undefined) {
        $scope.stateValid = null;
        $scope.genActive = null;
        $scope.feedbackActive = null;
        $scope.getEnvCurrentStatus();
    }

    $scope.setActive = function (family) {

        var headers = {
            "Content-Type": "application/json"
        };

        $http({
            method: 'PUT',
            url: "/v1/migration-manager/env-state/active?serviceFamily=" + family,
            headers: headers
        }).then(function (response) {

            if (family === "General") {
                $scope.genActive = true
            } else {
                $scope.feedbackActive = true
            }

            console.log(response);
        }, function (response) {
            $scope.message = 'Failed to fetch response';
            console.log(response);
        });
    }

    $scope.setStandBy = function (family) {

        var headers = {
            "Content-Type": "application/json"
        };

        $http({
            method: 'PUT',
            url: "/v1/migration-manager/env-state/standby?serviceFamily="+ family,
            headers: headers
        }).then(function (response) {

            if (family === "General") {
                $scope.genActive = false
            } else {
                $scope.feedbackActive = false
            }

            console.log(response);
        }, function (response) {
            $scope.message = 'Failed to fetch response';
            console.log(response);
        });
    }

    $scope.task = $interval($scope.getEnvCurrentStatus,60000);
});

app.controller('NavCtrl',
    ['$scope', '$location', function ($scope, $location) {
        $scope.navClass = function (page) {
            var currentRoute = $location.path().substring(1) || 'state';
            return page === currentRoute ? 'active' : '';
        };
    }]);



app.controller('UsersCtrl', function($scope, $http, $interval) {
    console.log('inside UsersCtrl controller');


    $scope.userRecords = [];

    $scope.getUsers = function () {

        $http({
            method: 'GET',
            url: "/iblockedu/api/users",
            headers: {"Content-Type": "application/json"}
        }).then(function (response) {

            $scope.userRecords = response.data.users;
        });
    }
    $scope.task = $interval($scope.getUsers,5000);

});


app.controller('postcarCtrl', function ($scope, $http) {
    $scope.name = null;
    $scope.phone = null;
    $scope.cpn = null;
    $scope.lblMsg = null;
    $scope.postdata = function (name, phone, cpn) {
        var data = {
        name: name,
        phone: phone,
        cpn: cpn
    };
    //Call the services
    $http.post('/iblockedu/api/user', JSON.stringify(data)).then(function (response) {
    if (response.data)
        $scope.msg = "Post Data Submitted Successfully!";
    }, function (response) {
        $scope.msg = "Service not Exists";
        $scope.statusval = response.status;
        $scope.statustext = response.statusText;
        $scope.headers = response.headers();
    });
    };
});