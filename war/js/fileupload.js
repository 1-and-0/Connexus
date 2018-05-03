


var fileUploadControler = [ '$scope', '$http', '$timeout', '$upload',  function($scope, $http, $timeout, $upload) {
	
	
	$scope.fileReaderSupported = window.FileReader != null;
	$scope.uploadRightAway = true;
	
	$scope.hasUploader = function(index) 
	{
		return $scope.upload[index] != null;
	};
	$scope.abort = function(index) {
		$scope.upload[index].abort(); 
		$scope.upload[index] = null;
	};
	
	$scope.onFileSelect = function($files) {
		$scope.selectedFiles = [];
		$scope.progress = [];
		if ($scope.upload && $scope.upload.length > 0) {
			for (var i = 0; i < $scope.upload.length; i++) {
				if ($scope.upload[i] != null) {
					$scope.upload[i].abort();
				}
			}
		}
		$scope.upload = [];
		$scope.uploadResult = [];
		$scope.selectedFiles = $files;
		$scope.dataUrls = [];
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			console.info($file);
			if (window.FileReader && $file.type.indexOf('image') > -1) {
			  	var fileReader = new FileReader();
		        fileReader.readAsDataURL($files[i]);
		        function setPreview(fileReader, index) {
		            fileReader.onload = function(e) {
		                $timeout(function() {
		                	$scope.dataUrls[index] = e.target.result;
		                });
		            }
		        }
		        setPreview(fileReader, i);
			}
			$scope.progress[i] = -1;
			if ($scope.uploadRightAway) {
				$scope.start(i);
			}
		}
	}
	
	$scope.start = function(index) {
		$scope.progress[index] = 0;
		{
			$scope.upload[index] = $upload.upload({
				url : getBlobStoreUrl(),
				data : {
					myModel : {"abc":"hurrah"}
				},
				file: $scope.selectedFiles[index],
				fileFormDataName: 'imageFile'
			}).then(function(response) {
				location.reload();
			}, null, function(evt) {
				$scope.progress[index] = parseInt(100.0 * evt.loaded / evt.total);
			});
		}
	}
} ];
