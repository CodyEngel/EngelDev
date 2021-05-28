var recentYouTubeVideos = new Vue({
    el: '#recentYouTubeVideos',
    data() {
        return {
            videos: null
        }
    },
    mounted() {
        axios
            .get("https://api.engel.dev/youtube/recentVideos")
            .then(response => {
                console.log(response);
                this.videos = response.data.videos.slice(0, 6).map(function (video) {
                    return {
                        "title": video.title,
                        "description": video.description,
                        "thumbnail": video.thumbnail,
                        "url": `https://youtube.com/v/${video.id}`
                    }
                });
            })
    }
});

var emailForm = new Vue({
    el: "#emailForm",
    data() {
        return {
            firstName: "",
            lastName: "",
            emailAddress: ""
        }
    },
    methods: {
        signUp: function (event) {
            console.log(`firstName: ${this.firstName}`)
            console.log(`lastName: ${this.lastName}`)
            console.log(`emailAddress: ${this.emailAddress}`)

            axios.post("https://api.engel.dev/marketing/email/recipient", {
                emailAddress: this.emailAddress,
                firstName: this.firstName,
                lastName: this.lastName
            }, {

            })
                .then(response => {
                    console.log(response);
                })
        }
    }
})