import React from 'react';

interface GitHubProjectLogoProps {
  account: string;
  project: string;
}

const GitHubProjectLogo: React.FC<GitHubProjectLogoProps> = ({ account, project }) => {
  const idleOpacity = 0.8;
  const handleClick = () => {
    window.open(`https://github.com/${account}/${project}`, '_blank');
  };

  return (
    <svg
      width="29"
      height="28"
      viewBox="0 0 29 28"
      fill="currentColor"
      xmlns="http://www.w3.org/2000/svg"
      onClick={handleClick}
      style={{ opacity: idleOpacity, cursor: 'pointer' }}
      onMouseEnter={(e) => {
        e.currentTarget.style.opacity = '1';
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.opacity = `${idleOpacity}`;
      }}
    >
      <path
        fillRule="evenodd"
        clipRule="evenodd"
        d="M14.0001 8.36223e-05C10.6764 0.000530922 7.46124 1.18346 4.9298 3.33728C2.39836 5.49109 0.715765 8.47528 0.183005 11.756C-0.349755 15.0368 0.302074 18.4 2.02189 21.2442C3.74171 24.0884 6.41732 26.2279 9.5701 27.2801C10.2701 27.4101 10.5701 26.9801 10.5701 26.6101C10.5701 26.2401 10.5701 25.4001 10.5701 24.2301C6.6801 25.0701 5.8601 22.3501 5.8601 22.3501C5.58655 21.4929 5.01082 20.7644 4.2401 20.3001C2.9701 19.4401 4.3401 19.4501 4.3401 19.4501C4.7837 19.5128 5.20712 19.676 5.57801 19.9273C5.9489 20.1786 6.25745 20.5113 6.4801 20.9001C6.86744 21.5948 7.51484 22.1073 8.27993 22.3248C9.04503 22.5423 9.86519 22.4471 10.5601 22.0601C10.6166 21.3506 10.9294 20.6858 11.4401 20.1901C8.3401 19.8301 5.0701 18.6301 5.0701 13.2701C5.04743 11.878 5.56333 10.5309 6.5101 9.51008C6.08156 8.30428 6.13167 6.98006 6.6501 5.81008C6.6501 5.81008 7.8201 5.43008 10.5001 7.24008C12.7916 6.61503 15.2086 6.61503 17.5001 7.24008C20.1701 5.43008 21.3401 5.81008 21.3401 5.81008C21.8585 6.98006 21.9086 8.30428 21.4801 9.51008C22.4269 10.5309 22.9428 11.878 22.9201 13.2701C22.9201 18.6501 19.6501 19.8301 16.5301 20.1801C16.8643 20.5188 17.122 20.9253 17.2859 21.3721C17.4498 21.8189 17.516 22.2956 17.4801 22.7701C17.4801 24.6401 17.4801 26.1501 17.4801 26.6101C17.4801 27.0701 17.7301 27.4201 18.4801 27.2801C21.637 26.2266 24.3154 24.083 26.035 21.2335C27.7545 18.3841 28.4027 15.0153 27.8634 11.7313C27.3241 8.4472 25.6325 5.46266 23.092 3.31285C20.5514 1.16304 17.3281 -0.0114138 14.0001 8.36223e-05Z"
        fill="white"
      />
    </svg>
  );
};

export default GitHubProjectLogo;