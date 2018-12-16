var json = {};
var outputs = [];
var verificador = null;
var progressDiv = null;
var converterBtn = document.getElementById('converter-btn');
document.getElementById('form').addEventListener('submit',callback, false);

function callback(e){
	e.preventDefault();
	let form = e.target;
	let formData = new FormData(form);
	let request = new XMLHttpRequest();
	
	request.onreadystatechange = function(){
		if(request.readyState === 4) {
			if(request.status === 200) { 
				json = JSON.parse(request.responseText);
				
				
				json.outputs.forEach(e => {
					outputs.push(e.id);
				});
				
				verificador = setInterval(verificaDisponibilidadeVideo, 5000);
				
			}
		}
    }
	let inputFile = document.getElementById('file');
	inputFile.disabled = true;
	converterBtn.disabled = true;
	
	progressDiv = document.getElementById('progress');
	progressDiv.style.display = 'block';
	
    request.open(form.method, form.action);
    request.send(formData);
	
}


function verificaDisponibilidadeVideo(){
	
	let videosDiv = document.getElementById('videos');
	
	if(outputs.length > 0){
		outputs.forEach(e => {
			let request = new XMLHttpRequest();
			request.onreadystatechange = function(){
				if(request.readyState === 4) {
					if(request.status === 200) {
						let output = JSON.parse(request.responseText);
						
						if(output.state === 'finished'){
							
							let videoDiv = document.createElement('div');
							videoDiv.className = "videos col s12";
							let video = document.createElement('video');
							video.setAttribute("controls", "controls");
							video.className = "responsive-video";
							let source = document.createElement('source');
							source.src =  output.url;
							
							let tipo = output.label.split(" ",1).toString();
							
							source.type = `video/${tipo}`;
							video.appendChild(source);
							videoDiv.appendChild(video);
							
							
							let detailsDiv = document.createElement('div');
							detailsDiv.className = "col s12";
							let detailsUl = document.createElement('ul');
							detailsUl.className = "collection";
							
							let formatLi = document.createElement('li');
								formatLi.className = "collection-item";
								formatLi.innerHTML = `Formato: ${output.label}`;
							let typeLi = document.createElement('li');
								typeLi.className = "collection-item";
								typeLi.innerHTML = `Tipo: ${output.type}`;
								
							
								detailsUl.appendChild(formatLi);
								detailsUl.appendChild(typeLi);
								
							videosDiv.appendChild(videoDiv);
							detailsDiv.appendChild(detailsUl);
							videosDiv.appendChild(detailsDiv);
							
							let index = outputs.indexOf(e);
							if(index > -1){
								outputs.splice(index,1);
							}
						}
					}
				}
			}
			request.open('get',`/verifyProgressOutput/${e}`);
	        request.send();
		});
		
	}else{
		converterBtn.style.display = 'none';
		let novoBtn = document.getElementById('novo-btn');
		novoBtn.style.display = 'block';
		progressDiv.style.display = 'none';
		clearInterval(verificador);
	}
	
}
function novoVideo(){
	window.location.reload();
}