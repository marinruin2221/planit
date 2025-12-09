import React from "@vitejs/plugin-react";
import Path from "path";
import { defineConfig } from "vite";

export default defineConfig
({
	plugins:[React()],
	root:".",
	base:"/",
	server:
	{
		port:5001,
		open:true,
		proxy:
		{
			"/api":"http://localhost:5002",
		},
	},
	resolve:
	{
		alias:
		{
			"@src":Path.resolve(__dirname,"src/"),
			"@components":Path.resolve(__dirname,"src/components"),
			"@css":Path.resolve(__dirname,"src/css"),
			"@hooks":Path.resolve(__dirname,"src/hooks"),
			"@img":Path.resolve(__dirname,"src/img"),
			"@pages":Path.resolve(__dirname,"src/pages"),
		},
	},
});