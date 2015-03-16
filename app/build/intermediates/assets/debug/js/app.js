/* app.js */
var app = angular.module('ocb', [                                 	
  'ui.router',
  'ui.bootstrap'
])
.config(function($stateProvider, $urlRouterProvider, $httpProvider) {

	//Configuration for route
	$urlRouterProvider.otherwise("/part1"); //If no definition, go to /dashboard
	
	$stateProvider
    .state('home', {
		templateUrl: 'index.html',
		controller : 'HomeController'
    })
    .state('home.part1', {
    	url: '/part1',
		templateUrl: '101.html',
		controller : function(){}
    })
    .state('home.part2', {
    	url: '/part2',
		templateUrl: '201.html',
		controller : function(){}
    })
    .state('home.part3', {
    	url: '/part3',
    	templateUrl: '301.html',
		controller : function(){}
    })
    .state('home.page', {
    	url: '/:page',
    	templateUrl: function(stateParams){
            return stateParams.page + '.html';
        },
		controller : function(){}
    })
})
.controller('HomeController', function($scope){
    $scope.parts = [
        {id:101, key:"part1", title:"Part1 - Australia and its people"},
        {id:201, key:"part2", title:"Part2 - Australia's democratic beliefs, rights and liberties"},
        {id:301, key:"part3", title:"Part3 - Government and the law in Australia"},
    ];

    $scope.menu = {
        "part1": [
            {id:101, title:"Our People"},
            {id:102, title:"Australia's states and territories"},
            {id:103, title:"Traditions and Symbols"},
            {id:104, title:"Australia's flags"},
            {id:105, title:"Australia's national anthem"}
        ],
        "part2": [
            {id:201, title:"Our democratic beliefs"},
            {id:202, title:"Our freedoms"},
            {id:203, title:"Our equalities"},
            {id:204, title:"Responsibilities and privileges of Australian citizenship"},
            {id:205, title:"Our democratic beliefs"}
        ],
        "part3":[
            {id:301, title:"How do I have my say?"},
            {id:302, title:"How did we establish our system of government?"},
            {id:303, title:"How is the power of government controlled?"},
            {id:304, title:"Who is Australia’s Head of State?"},
            {id:305, title:"Who are some of Australia’s leaders?"},
            {id:306, title:"How is Australia governed?"},
            {id:307, title:"What do the three levels of government do?"},
            {id:308, title:"What role do political parties play in the way Australia is governed?"},
            {id:309, title:"How are laws administered?"}
        ]
    };

    $scope.currentPart;
    $scope.setPart = function(part){
        $scope.currentPart = part;
        $scope.currentMenu = $scope.menu[part.key];
        $scope.navCollapsed = true;
    }

    $scope.setPart($scope.parts[0]);
})