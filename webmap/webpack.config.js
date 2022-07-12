const path = require('path')

module.exports = {
    devtool: 'source-map',
    entry: './src/Pl3xMap.ts',
    externals: {
        "leaflet": "L"
    },
    mode: 'production',
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: 'ts-loader',
                include: [path.resolve(__dirname, 'src')]
            }
        ]
    },
    output: {
        publicPath: 'public',
        filename: 'pl3xmap.js',
        path: path.resolve(__dirname, 'public')
    },
    resolve: {
        extensions: ['.ts', '.js']
    }
}
