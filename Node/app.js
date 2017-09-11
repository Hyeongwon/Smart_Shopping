/**
 * Created by byunhyeongwon on 2017. 9. 8..
 */

// Express Module
var express = require('express')
    , http = require('http')
    , path = require('path');

var bodyPaser = require('body-parser')
    , static = require('serve-static');

var expressErrorHandler = require('express-error-handler');

// 모듈로 분리한 설정 파일 불러오기
var config = require('./config/config');

// 모듈로 분리한 데이터베이스 파일 불러오기
var database = require('./database/database');

// 모듈로 분리한 라우팅 파일 불러오기
var route_loader = require('./routes/route_loader');

var app = express();

app.set('port', process.env.PORT || 3000);

app.use(bodyPaser.urlencoded({ extended : false}));
app.use(bodyPaser.json());
app.use('/public', static(path.join(__dirname, 'public')));

// Router
var router = express.Router();
route_loader.init(app, router);

// 404Error page
var errorHandler = expressErrorHandler({

    static: {
        '404' : 'public/404.html'
    }
});

app.use(expressErrorHandler.httpError(404));
app.use(errorHandler);

//===== Start Server =====//

// database disconnect when process exit
process.on('SIGTERM', function () {
    console.log("process exit.");
});

app.on('close', function () {
    console.log("Express server object exit.");
});

http.createServer(app).listen(app.get('port'), function() {
    console.log("Server Start...!!! Port : " + app.get('port'));

    database.init(app, config);
});

