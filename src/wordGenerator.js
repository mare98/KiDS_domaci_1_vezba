
const fetch = require('node-fetch');
const fs = require('fs');

for(let i = 0; i < 10; i++ ) {
    const filename = i + '.txt';

    const upperBound = 10 + Math.floor(Math.random() * 5);
    for (let j = 0; j < upperBound; j++) {
        fetch("https://www.randomtextgenerator.com/", {
            "headers": {
                "accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
                "accept-language": "en-US,en;q=0.9",
                "cache-control": "max-age=0",
                "content-type": "application/x-www-form-urlencoded",
                "sec-ch-ua": "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"",
                "sec-ch-ua-mobile": "?0",
                "sec-fetch-dest": "document",
                "sec-fetch-mode": "navigate",
                "sec-fetch-site": "same-origin",
                "sec-fetch-user": "?1",
                "upgrade-insecure-requests": "1",
                "cookie": "ci_session=a%3A7%3A%7Bs%3A10%3A%22session_id%22%3Bs%3A32%3A%222fd0c1af428719815f19ffcf07e40ce4%22%3Bs%3A10%3A%22ip_address%22%3Bs%3A13%3A%2277.105.15.133%22%3Bs%3A10%3A%22user_agent%22%3Bs%3A114%3A%22Mozilla%2F5.0+%28Windows+NT+10.0%3B+Win64%3B+x64%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Chrome%2F89.0.4389.90+Safari%2F537.36%22%3Bs%3A13%3A%22last_activity%22%3Bi%3A1616948919%3Bs%3A9%3A%22user_data%22%3Bs%3A0%3A%22%22%3Bs%3A9%3A%22text_mode%22%3Bs%3A5%3A%22plain%22%3Bs%3A8%3A%22language%22%3Bs%3A2%3A%22en%22%3B%7D007e050f1d7c509a829653ea0a3428486da40c5d; __utmc=174110625; __gads=ID=67664363dfe53ddf-2272c2eee5ba00a5:T=1616948920:RT=1616948920:S=ALNI_MZj3BKnH_fHMkJBmLtGQfFzbDm6qw; __utma=174110625.870334653.1616948920.1616948920.1616951990.2; __utmz=174110625.1616951990.2.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); __utmt=1; __utmb=174110625.2.10.1616951990"
            },
            "referrer": "https://www.randomtextgenerator.com/",
            "referrerPolicy": "strict-origin-when-cross-origin",
            "body": "text_mode=plain&language=none&Go=Go",
            "method": "POST",
            "mode": "cors"
        }).then(response => response.text()).then(text => {
            const regExp = /(?:<textarea id="generatedtext">)([^]*)(?:<\/textarea>)/gm;
            const arr = regExp.exec(text);

            fs.appendFile(filename, arr[1], function (err) {
                if (err) throw err;
            });

        });
    }
    fs.stat(filename, (err, fileStats) => {
        if (err) {
            console.log(err);
        } else {
            console.log(`File size: ${fileStats.size} B`);
        }
    });
}
