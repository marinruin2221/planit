import Client from "react-dom/client";

import App from "@src/App.jsx";

const body = document.querySelector("body");
const root = Client.createRoot(body);

root.render
(
	<App></App>
)