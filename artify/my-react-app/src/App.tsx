import "./App.css";
import { useState, useEffect } from "react";
import ResultCard from "./components/ResultCard";
import InputArea from "./components/InputArea";
import Header from "./components/Header";
import Footer from "./components/Footer";
import Wiki from "./components/Wiki";
import { useLocation } from "react-router-dom";
import axios from "axios";
import { useAuth } from "./AuthContext.tsx";
import Toast from "./components/Toast.tsx";

function App() {
  const [userInput, setUserInput] = useState("");
  const [searchResult, setSearchResult] = useState(null);
  const [artistWiki, setArtistWiki] = useState(null);
  const [artworks, setArtworks] = useState(null);
  const [similarArtists, setSimilarArtists] = useState(null);

  const [showWiki, setShowWiki] = useState(false);
  const [showCaraousel, setShowCaraousel] = useState(false);
  const [infoSpinner, setInfoSpinner] = useState(false);

  const handleUserInput = (event: any) => {
    setUserInput(event);
  };

  useEffect(() => {}, []);

  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const searchId = queryParams.get("q");
  const searchImg = queryParams.get("img");
  const { userData } = useAuth();

  useEffect(() => {
    if (searchId) {
      const id = searchId;
      const img = searchImg;

      const req1 = axios.post(
        "https://hw3-cs571-spring-25.uw.r.appspot.com/api/about",
        { uid: id }
      );
      const req2 = axios.post(
        "https://hw3-cs571-spring-25.uw.r.appspot.com/api/artworks",
        { uid: id }
      );

      Promise.all([req1, req2])
        .then(([res1, res2]) => {
          setArtistWiki({ ...res1.data, img });
          setArtworks(res2.data);
        })
        .catch((err) => {
          console.log("Error while requesting artist wiki or artworks", err);
        });

      if (userData) {
        axios
          .post("https://hw3-cs571-spring-25.uw.r.appspot.com/api/similar", {
            uid: id,
          })
          .then((res) => {
            setSimilarArtists(res.data);
          })
          .catch((err) => {
            console.log("Error while sending similar artist query", err);
          });
      }

      setShowWiki(true);
      setShowCaraousel(false);
    }
  }, [searchId, searchImg]);

  return (
    <div>
      <Header />
      <Toast />
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-md-11">
            <InputArea
              userInput={userInput}
              handleChange={handleUserInput}
              setSearchResult={setSearchResult}
              setShowWiki={setShowWiki}
              setShowCaraousel={setShowCaraousel}
            />
            {showCaraousel && (
              <ResultCard
                searchResult={searchResult}
                setArtistWiki={setArtistWiki}
                setArtworks={setArtworks}
                setShowWiki={setShowWiki}
                setShowCaraousel={setShowCaraousel}
                setSimilarArtists={setSimilarArtists}
                setInfoSpinner={setInfoSpinner}
              />
            )}
            {showWiki && (
              <Wiki
                artistWiki={artistWiki}
                artworks={artworks}
                setShowCaraousel={setShowCaraousel}
                similarArtists={similarArtists}
                setArtistWiki={setArtistWiki}
                setArtworks={setArtworks}
                showWiki={showWiki}
                setShowWiki={setShowWiki}
                setSimilarArtists={setSimilarArtists}
                infoSpinner={infoSpinner}
                setInfoSpinner={setInfoSpinner}
              />
            )}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default App;
