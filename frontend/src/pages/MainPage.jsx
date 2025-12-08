import React from "react";

import Header from "@layout/Header.jsx";
import Footer from "@layout/Footer.jsx";

export default function MainPage()
{
	return <React.Fragment>
		<Header/>

		<form className="flex justify-center items-center gap-2">
			<div className="flex flex-1 flex-col gap-1 p-2 rounded-sm bg-gray-100">
				<label className="text-xs text-gray-500">여행지, 숙소 검색</label>
				<input className="text-sm outline-0"/>
			</div>
			<div className="flex flex-1 flex-col gap-1 p-2 rounded-sm bg-gray-100">
				<label className="text-xs text-gray-500">가는날</label>
				<input className="text-sm outline-0"/>
			</div>
			<div className="flex flex-1 flex-col gap-1 p-2 rounded-sm bg-gray-100">
				<label className="text-xs text-gray-500">오는날</label>
				<input className="text-sm outline-0"/>
			</div>
			<div className="flex flex-1 flex-col gap-1 p-2 rounded-sm bg-gray-100">
				<label className="text-xs text-gray-500">인원</label>
				<input className="text-sm outline-0"/>
			</div>
			<button className="flex-1 p-2 rounded-sm text-white bg-blue-500">검색하기</button>
		</form>

		<Footer/>
	</React.Fragment>
} 