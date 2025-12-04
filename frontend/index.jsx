import Client from "react-dom/client";

import App from "@src/App.jsx";

import "@css/tailwind.css";

const body = document.querySelector("body");
const root = Client.createRoot(body);

root.render
(
	<App></App>
)