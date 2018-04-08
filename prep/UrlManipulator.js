var fs = require('fs')

var data = fs.readFileSync('TimelineUrls.txt', 'utf8')
var lines = data.split('\n').filter(line => line != "")
var chunks = lines.map(line => line.split('&'))
var parsed = chunks.map(line => {
    return { id: line[0], month: line[1].substring(6), script: line[2].substring(7, line[2].length-1)}
})

console.log(parsed)