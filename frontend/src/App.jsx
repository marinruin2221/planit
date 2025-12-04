import React , { useEffect } from "react";
import { BrowserRouter , Routes , Route } from "react-router-dom";

import MainPage from "@pages/MainPage.jsx";
import MyPage from "@pages/MyPage.jsx";

export default function App()
{
	return <React.Fragment>
		<BrowserRouter>
			<Routes>
				<Route path="/" element={<MainPage/>}></Route>
				<Route path="/main" element={<MainPage/>}></Route>
				<Route path="/mypage" element={<MyPage/>}></Route>
			</Routes>
		</BrowserRouter>
	</React.Fragment>
}